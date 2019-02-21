package main;

import main.geom.Vector;
import main.io.VTKWriter;
import main.mesh.Cell;
import main.mesh.Mesh;
import main.mesh.factory.Structured2DMesh;
import main.physics.bc.BoundaryCondition;
import main.physics.bc.ExtrapolatedBC;
import main.physics.goveqn.GoverningEquations;
import main.physics.goveqn.factory.VolumeFractionAdvectionEquations;
import main.solver.*;
import main.solver.convection.ConvectionResidual;
import main.solver.convection.reconstructor.SolutionReconstructor;
import main.solver.convection.reconstructor.VKLimiterReconstructor;
import main.solver.convection.riemann.HLLRiemannSolver;
import main.solver.convection.riemann.RiemannSolver;
import main.solver.diffusion.DiffusionResidual;
import main.solver.problem.ProblemDefinition;
import main.solver.time.*;
import main.util.DoubleArray;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class SolverTransient2DVolumeFractionAdvectionTest {

    private ProblemDefinition problemDef = new ProblemDefinition() {
        private final double minX = -5;
        private final double minY = -5;
        private final double Lx = 10;
        private final double Ly = 10;
        private final int numXCells = 40;
        private final int numYCells = 40;

        private final String description = "2D Volume fraction advection.";
        private final GoverningEquations govEqn = new VolumeFractionAdvectionEquations();
        private final Mesh mesh = create2DMesh();

        private Mesh create2DMesh() {
            int numXNodes = numXCells + 1;
            int numYNodes = numYCells + 1;
            File tempMeshFile = new File("test/test_data/temp.cfdu");

            try (FileWriter fileWriter = new FileWriter(tempMeshFile);
                 PrintWriter writer = new PrintWriter(fileWriter)) {
                writer.write("dimension = 2\n");
                writer.write("mode = ASCII\n");
                writer.printf("xi = %d\n", numXNodes);
                writer.printf("eta = %d\n", numYNodes);
                for (int i = 0; i < numXNodes; i++) {
                    double x = minX + i / (numXNodes - 1.0) * Lx;
                    for (int j = 0; j < numYNodes; j++) {
                        double y = minY + j / (numYNodes - 1.0) * Ly;
                        writer.printf("%-20.15f %-20.15f %-20.15f\n", x, y, 0.0);
                    }
                }
            } catch (IOException e) {
                System.out.println("Unable to create mesh.");
            }

            BoundaryCondition extropolatedBC = new ExtrapolatedBC(govEqn);
            Mesh mesh = null;
            try {
                mesh = new Structured2DMesh(tempMeshFile, govEqn.numVars(), extropolatedBC, extropolatedBC, extropolatedBC, extropolatedBC);
                if (!tempMeshFile.delete()) {
                    System.out.println("Unable to delete temporary file: " + tempMeshFile);
                }
            } catch (FileNotFoundException e) {
                System.out.println("Mesh file is not found.");
            }
            return mesh;
        }

        private CellNeighborCalculator cellNeighborCalculator = new FaceBasedCellNeighbors();
        private CellGradientCalculator cellGradientCalculator = new LeastSquareCellGradient(
                mesh, cellNeighborCalculator);
        private final SolutionReconstructor reconstructor
                = new VKLimiterReconstructor(mesh, cellNeighborCalculator);
        private final RiemannSolver riemannSolver = new HLLRiemannSolver(govEqn);
        List<ResidualCalculator> residuals = List.of(
                new ConvectionResidual(reconstructor, riemannSolver, mesh),
                new DiffusionResidual(mesh, govEqn));
        private final SpaceDiscretization spaceDiscretization = new SpaceDiscretization(
                mesh, cellGradientCalculator, residuals);
        TimeStep timeStep = new LocalTimeStep(mesh, govEqn);
        private final TimeIntegrator timeIntegrator = new ExplicitSSPRK2TimeIntegrator(
                mesh, spaceDiscretization, timeStep, govEqn.numVars());

        private final Convergence convergence
                = new Convergence(DoubleArray.newFilledArray(govEqn.numVars(), 1e-3));

        @Override
        public String description() {
            return description;
        }

        @Override
        public GoverningEquations govEqn() {
            return govEqn;
        }

        @Override
        public Mesh mesh() {
            return mesh;
        }

        @Override
        public SolutionInitializer solutionInitializer() {
            double radius = 0.5;
            return new FunctionInitializer(p -> {
                double dist = p.toVector().mag();
                double C = dist < radius ? 1 : 0;
                return new double[]{C, 1, 0.5, 0, 0, 0, 0};
            });
        }

        @Override
        public TimeIntegrator timeIntegrator() {
            return timeIntegrator;
        }

        @Override
        public Convergence convergence() {
            return convergence;
        }

        @Override
        public Config config() {
            Config config = new Config();
            try {
                config.setWorkingDirectory(new File("test/test_data/volume_fraction_advection/"));
            } catch (IOException e) {
                System.out.println("Unable to create working directory.");
            }
            return config;
        }
    };

    @Test
    public void solver() {
        ProblemDefinition problem = this.problemDef;
        Mesh mesh = problem.mesh();
        GoverningEquations govEqn = problem.govEqn();
        Config config = problem.config();
        Convergence convergence = problem.convergence();
        TimeIntegrator timeIntegrator = problem.timeIntegrator();
        timeIntegrator.setCourantNum(1.0);

        double real_dt = 0.01;
        int numRealTimeSteps = 20;
        TimeDiscretization timeDiscretization = new TwoPointTimeDiscretization(
                mesh, govEqn, real_dt);
        timeIntegrator.setTimeDiscretization(timeDiscretization);

        problem.solutionInitializer().initialize(mesh, govEqn);

        int[] iterationCount = new int[numRealTimeSteps];

        VTKWriter vtkWriter = new VTKWriter(mesh, govEqn);
        double time = 0;
        for (int realTimeStep = 0; realTimeStep < numRealTimeSteps; realTimeStep++) {
            System.out.println("Time: " + time);
            vtkWriter.write(new File(config.getWorkingDirectory(), String.format("sol%05d.vtu", realTimeStep)));
            int totalIterations = 0;
            for (int iter = 0; iter < config.getMaxIterations(); iter++) {
                timeIntegrator.updateCellAverages();
                setupVr(mesh);
                double[] totalResidual = timeIntegrator.currentTotalResidual(config.getConvergenceNorm());
                System.out.println(iter + ": " + Arrays.toString(totalResidual));
                if (convergence.hasConverged(totalResidual)) {
                    System.out.println("Converged.");
                    totalIterations = iter + 1;
                    break;
                }
            }
            if (totalIterations == 0) {
                totalIterations = config.getMaxIterations();
            }
            iterationCount[realTimeStep] = totalIterations;

            timeDiscretization.shiftSolution();

            time += real_dt;
            if (realTimeStep == 0) {
                timeDiscretization = new ThreePointTimeDiscretization(mesh, govEqn, real_dt);
                timeIntegrator.setTimeDiscretization(timeDiscretization);
            }
        }
        vtkWriter.write(new File(config.getWorkingDirectory(), String.format("sol%05d.vtu", numRealTimeSteps)));

        int[] expectedIterationCount = {
                14, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 10, 10, 10, 10
        };
        System.out.println(Arrays.toString(iterationCount));

        Assert.assertArrayEquals(expectedIterationCount, iterationCount);
    }

    private void setupVr(Mesh mesh) {
        mesh.cellStream().forEach(this::setupVr);
    }

    private void setupVr(Cell cell) {
        double C = cell.U[0];
        double u = cell.U[1];
        double v = cell.U[2];
        double w = cell.U[3];

        Vector gradC = cell.gradientU[0];
        double magGradC = gradC.mag();

        Vector zeroVector = new Vector(0, 0, 0);
        Vector unitGradC = magGradC > 1e-6 ? gradC.mult(1.0 / magGradC) : zeroVector;

        Vector V = new Vector(u, v, w);
        double magV = V.mag();
        Vector unitV = magV > 1e-6 ? V.mult(1.0 / magV) : zeroVector;

        double Ca = 0.5 * Math.sqrt(Math.abs(unitGradC.dot(unitV)));
        double scalar = Ca * magV;
        Vector Vc = unitGradC.mult(scalar);

        Vector Vr = Vc.mult(1 - C);

        cell.U[4] = Vr.x;
        cell.U[5] = Vr.y;
        cell.U[6] = Vr.z;
    }
}