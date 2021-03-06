package main.solver.convection.reconstructor;

import main.geom.Point;
import main.geom.Vector;
import main.mesh.Cell;
import main.mesh.Mesh;
import main.mesh.Node;
import main.physics.goveqn.GoverningEquations;
import main.physics.goveqn.Limits;
import main.solver.CellNeighborCalculator;
import main.util.Util;

import static main.util.DoubleArray.add;

public class VKLimiterReconstructor implements SolutionReconstructor {
    private final Mesh mesh;
    private final CellNeighborCalculator neighCalc;
    private final Cell[][] neighbors;
    private final GoverningEquations govEqn;

    public VKLimiterReconstructor(Mesh mesh, GoverningEquations govEqn, CellNeighborCalculator neighCalc) {
        this.govEqn = govEqn;
        int numCells = mesh.cells().size();
        this.mesh = mesh;
        this.neighCalc = neighCalc;
        this.neighbors = new Cell[numCells][];

        mesh.cellStream().forEach(this::setup);
    }

    private void setup(Cell cell) {
        neighbors[cell.index()] = neighCalc.calculateFor(cell).toArray(new Cell[0]);

        for (int var = 0; var < cell.U.length; var++) {
            cell.reconstructCoeffs[var] = new double[3];
        }
    }

    @Override
    public void reconstruct() {
        mesh.cellStream().forEach(this::reconstructCell);
    }

    private void reconstructCell(Cell cell) {
        Vector[] gradients = cell.gradientU;
        for (int var = 0; var < cell.U.length; var++) {
            reconstructVar(cell, gradients, var);
        }
    }

    private void reconstructVar(Cell cell, Vector[] gradients, int var) {
        Limits physicalLimits = govEqn.physicalLimits()[var];

        Cell[] cellNeighbors = neighbors[cell.index()];
        double ui = cell.U[var];
        double uMax = ui;
        double uMin = ui;
        for (Cell neighbor : cellNeighbors) {
            double value = neighbor.U[var];
            if (value > uMax) uMax = value;
            if (value < uMin) uMin = value;
        }
        uMax = Util.clip(uMax, physicalLimits.min, physicalLimits.max);
        double duMax = uMax - ui;

        uMin = Util.clip(uMin, physicalLimits.min, physicalLimits.max);
        double duMin = uMin - ui;

        Vector gradient_unlimited = gradients[var];
        double phi_i = Double.POSITIVE_INFINITY;
        for (Node node : cell.nodes) {
            double nodeValue = reconstructValueAt(cell, var, gradient_unlimited, node.location());
            double nodePhi = Phi(duMin, duMax, ui, nodeValue);
            if (nodePhi < phi_i) phi_i = nodePhi;
        }

        Vector gradient_limited = gradient_unlimited.mult(phi_i);
        cell.reconstructCoeffs[var][0] = gradient_limited.x;
        cell.reconstructCoeffs[var][1] = gradient_limited.y;
        cell.reconstructCoeffs[var][2] = gradient_limited.z;
    }

    private double reconstructValueAt(Cell cell, int var, Vector gradient, Point at) {
        Vector distanceVector = new Vector(cell.shape.centroid, at);
        return cell.U[var] + gradient.dot(distanceVector);
    }

    private double Phi(double duMin, double duMax, double ui, double uj) {
        double deltaMinus = (uj - ui);
        if (uj - ui > 0) {
            return phi(duMax, deltaMinus);
        } else if (uj - ui < 0) {
            return phi(duMin, deltaMinus);
        } else {
            return 1;
        }
    }

    private double phi(double dp, double dm) {
        double y = dp / dm;
        return (y * y + 2.0 * y) / (y * y + y + 2.0);
    }

    @Override
    public double[] conservativeVars(Cell cell, Point atPoint) {
        int numVars = cell.U.length;
        Vector r = new Vector(cell.shape.centroid, atPoint);
        double[] dU = new double[numVars];
        for (int var = 0; var < numVars; var++) {
            double du_dx = cell.reconstructCoeffs[var][0];
            double du_dy = cell.reconstructCoeffs[var][1];
            double du_dz = cell.reconstructCoeffs[var][2];
            Vector gradient = new Vector(du_dx, du_dy, du_dz);

            dU[var] = gradient.dot(r);
        }

        return add(cell.U, dU);
    }
}
