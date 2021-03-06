package main.physics.bc;

import main.geom.Vector;
import main.mesh.Cell;
import main.mesh.Face;
import main.mesh.Surface;
import main.physics.goveqn.factory.ArtificialCompressibilityEquations;
import main.util.DoubleArray;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class PressureOutletBCTest {

    @Test
    public void setGhostCellValues() {
        ArtificialCompressibilityEquations govEqn = new ArtificialCompressibilityEquations(5.6, 4e-4,
                new Vector(-4, -9.2, 7));
        double bndPr = 6;
        PressureOutletBC bc = new PressureOutletBC(govEqn, bndPr);

        Cell left = new Cell(null, null, null, govEqn.numVars());
        Cell right = new Cell(null, null, null, govEqn.numVars());
        Face face = new Face(null, null, null, left, right, govEqn.numVars());

        double pi = 5;
        double ui = 54;
        double vi = -80.2;
        double wi = 784;

        double beta = 1.0;

        double ghostPr = 2 * bndPr - pi;

        double[] expectedGhostConservativeVars = {
                ghostPr / beta, ui, vi, wi
        };

        double[] insideConservativeVars = {
                pi / beta, ui, vi, wi
        };
        DoubleArray.copy(insideConservativeVars, face.left.U);
        bc.setGhostCellValues(face);

        assertArrayEquals(expectedGhostConservativeVars, face.right.U, 1e-15);
    }

    @Test
    public void convectiveFlux() {
        ArtificialCompressibilityEquations govEqn = new ArtificialCompressibilityEquations(5.6, 4e-4,
                new Vector(-4, -9.2, 7));
        double bndPr = 6;
        PressureOutletBC bc = new PressureOutletBC(govEqn, bndPr);

        Surface surface = new Surface(654.0, null, new Vector(4, -1, .25).unit());

        Cell left = new Cell(null, null, null, govEqn.numVars());
        Cell right = new Cell(null, null, null, govEqn.numVars());
        Face face = new Face(null, null, surface, left, right, govEqn.numVars());

        double pi = 5;
        double ui = 54;
        double vi = -80.2;
        double wi = 784;

        double beta = 1.0;

        double[] expectedFlux = govEqn.convection().flux(new double[]{bndPr / beta, ui, vi, wi}, surface.unitNormal());

        double[] insideConservativeVars = {
                pi / beta, ui, vi, wi
        };
        DoubleArray.copy(insideConservativeVars, face.left.U);
        assertArrayEquals(expectedFlux, bc.convectiveFlux(face), 1e-15);
    }
}