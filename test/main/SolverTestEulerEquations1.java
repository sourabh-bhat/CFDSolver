package main;

import main.mesh.Mesh;
import main.mesh.factory.Unstructured2DMesh;
import main.physics.bc.ExtrapolatedBC;
import main.physics.bc.InletBC;
import main.physics.bc.InviscidWallBC;
import main.physics.goveqn.GoverningEquations;
import main.physics.goveqn.factory.EulerEquations;
import main.solver.*;
import main.solver.problem.ProblemDefinition;
import main.solver.time.ExplicitEulerTimeIntegrator;
import main.solver.time.GlobalTimeStep;
import main.solver.time.TimeIntegrator;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SolverTestEulerEquations {

    ProblemDefinition problem = new ProblemDefinition() {
        private final String description = "Euler Equations - Diamond Airfoil.";
        private final EulerEquations govEqn = new EulerEquations(1.4, 287);
        private Mesh mesh;

        {
            try {
                mesh = new Unstructured2DMesh(
                        new File("test/test_data/mesh_diamond_airfoil_unstructured_2d.cfdu"),
                        govEqn.numVars(), Map.of(
                        "Top-Bottom", new ExtrapolatedBC(govEqn),
                        "Right", new ExtrapolatedBC(govEqn),
                        "Airfoil", new InviscidWallBC(govEqn),
                        "Inlet", new InletBC(govEqn,
                                time -> new InletBC.InletProperties(700.0, 1.0, 101325.0))
                ));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        private final double u = 700.0;
        private final double rho = 1.0;
        private final double rhoE = 101325.0 / (1.4 - 1.0) / 1.0 + u * u / 2.0;
        private final SolutionInitializer solutionInitializer = new FunctionInitializer(
                p -> new double[]{rho, rho * u, 0.0, 0.0, rhoE});
        ResidualCalculator convectiveCalculator = new ConvectiveResidual(new PiecewiseConstantSolutionReconstructor(),
                new RusanovRiemannSolver(govEqn), mesh);
        private final TimeIntegrator timeIntegrator = new ExplicitEulerTimeIntegrator(mesh,
                List.of(convectiveCalculator), new GlobalTimeStep(mesh, govEqn), govEqn.numVars());
        private final Convergence convergence = new Convergence(new double[govEqn.numVars()]);
        private final Config config = new Config();

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
            return solutionInitializer;
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
            config.setMaxIterations(1);
            return config;
        }
    };

    @Test
    public void solver() {
        ProblemDefinition problem = this.problem;
        Mesh mesh = problem.mesh();
        problem.solutionInitializer().initialize(mesh);
        TimeIntegrator timeIntegrator = problem.timeIntegrator();
        Config config = problem.config();
        for (int iter = 0; iter < config.getMaxIterations(); iter++) {
            timeIntegrator.updateCellAverages(0);
            System.out.println(Arrays.toString(timeIntegrator.currentTotalResidual(Norm.ONE_NORM)));
        }
    }
}