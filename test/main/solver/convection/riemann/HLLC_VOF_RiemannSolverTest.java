package main.solver.convection.riemann;

import main.geom.Vector;
import main.physics.goveqn.factory.ArtificialCompressibilityVOFEquations;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class HLLC_VOF_RiemannSolverTest {

    @Test
    public void flux() {
        double rho1 = 1000.0;
        double rho2 = 1.125;

        double mu1 = 8.9e-4;
        double mu2 = 1.983e-5;

        double beta = 500;

        Vector gravity = new Vector(-1, -5, 3.0);

        ArtificialCompressibilityVOFEquations govEqn
                = new ArtificialCompressibilityVOFEquations(rho1, mu1, rho2, mu2, gravity, beta);

        HLLC_VOF_RiemannSolver solver = new HLLC_VOF_RiemannSolver(govEqn);

        double[] UL = {
                120, 198, 10, 0, 0.4
        };
        double[] UR = {
                12, 18, 320, 0, 0.5
        };
        Vector faceNormal = new Vector(1, 1, 0).unit();

        // Calculated using the 2D structured solver code (except the 4th component)
        assertArrayEquals(new double[]{2648.4343430802633, 1.6849201174788963E7, 1.6845083545975972E7, 0, 8.760912368072734},
                solver.flux(UL, UR, faceNormal), 1e-15);


        // UL, UR interchanged
        UL = new double[]{
                12, 18, 320, 0, 0.5
        };
        UR = new double[]{
                120, 198, 10, 0, 0.4
        };

        // Calculated using the 2D structured solver code (except the 4th component)
        assertArrayEquals(new double[]{-2749.8546588637614, 1.6833997209372137E7, 1.683813175542595E7, 0.0, -8.796906497477327},
                solver.flux(UL, UR, faceNormal), 1e-15);
    }

    @Test
    public void calculateUnitTangents() {
        double rho1 = 450;
        double rho2 = 100;

        double mu1 = 1e-3;
        double mu2 = 2.5e-3;

        double beta = 1.0;

        Vector gravity = new Vector(-1, -5, 3.0);

        ArtificialCompressibilityVOFEquations govEqn
                = new ArtificialCompressibilityVOFEquations(rho1, mu1, rho2, mu2, gravity, beta);

        HLLC_VOF_RiemannSolver solver = new HLLC_VOF_RiemannSolver(govEqn);
        Vector unitNormal = new Vector(1, 0, 0).unit();

        Vector[] tangents = solver.calculateUnitTangents(unitNormal);

        assertEquals(1, tangents[0].mag(), 1e-15);
        assertEquals(1, tangents[1].mag(), 1e-15);
        assertEquals(0, tangents[0].dot(tangents[1]), 1e-15);
        assertEquals(0, unitNormal.dot(tangents[0]), 1e-15);
        assertEquals(0, unitNormal.dot(tangents[1]), 1e-15);

        // Another test
        unitNormal = new Vector(0, 1, 0).unit();

        tangents = solver.calculateUnitTangents(unitNormal);

        assertEquals(1, tangents[0].mag(), 1e-15);
        assertEquals(1, tangents[1].mag(), 1e-15);
        assertEquals(0, tangents[0].dot(tangents[1]), 1e-15);
        assertEquals(0, unitNormal.dot(tangents[0]), 1e-15);
        assertEquals(0, unitNormal.dot(tangents[1]), 1e-15);

        // Another test
        unitNormal = new Vector(0, 0, 1).unit();

        tangents = solver.calculateUnitTangents(unitNormal);

        assertEquals(1, tangents[0].mag(), 1e-15);
        assertEquals(1, tangents[1].mag(), 1e-15);
        assertEquals(0, tangents[0].dot(tangents[1]), 1e-15);
        assertEquals(0, unitNormal.dot(tangents[0]), 1e-15);
        assertEquals(0, unitNormal.dot(tangents[1]), 1e-15);

        // Another test
        unitNormal = new Vector(0, 1, 1).unit();

        tangents = solver.calculateUnitTangents(unitNormal);

        assertEquals(1, tangents[0].mag(), 1e-15);
        assertEquals(1, tangents[1].mag(), 1e-15);
        assertEquals(0, tangents[0].dot(tangents[1]), 1e-15);
        assertEquals(0, unitNormal.dot(tangents[0]), 1e-15);
        assertEquals(0, unitNormal.dot(tangents[1]), 1e-15);

        // Another test
        unitNormal = new Vector(1, 1, 1).unit();

        tangents = solver.calculateUnitTangents(unitNormal);

        assertEquals(1, tangents[0].mag(), 1e-15);
        assertEquals(1, tangents[1].mag(), 1e-15);
        assertEquals(0, tangents[0].dot(tangents[1]), 1e-15);
        assertEquals(0, unitNormal.dot(tangents[0]), 1e-15);
        assertEquals(0, unitNormal.dot(tangents[1]), 1e-15);

        // Another test
        unitNormal = new Vector(1, 2, 1).unit();

        tangents = solver.calculateUnitTangents(unitNormal);

        assertEquals(1, tangents[0].mag(), 1e-15);
        assertEquals(1, tangents[1].mag(), 1e-15);
        assertEquals(0, tangents[0].dot(tangents[1]), 1e-15);
        assertEquals(0, unitNormal.dot(tangents[0]), 1e-15);
        assertEquals(0, unitNormal.dot(tangents[1]), 1e-15);

        // Another test
        unitNormal = new Vector(2, 3, 0).unit();

        tangents = solver.calculateUnitTangents(unitNormal);

        assertEquals(1, tangents[0].mag(), 1e-15);
        assertEquals(1, tangents[1].mag(), 1e-15);
        assertEquals(0, tangents[0].dot(tangents[1]), 1e-15);
        assertEquals(0, unitNormal.dot(tangents[0]), 1e-15);
        assertEquals(0, unitNormal.dot(tangents[1]), 1e-15);
    }
}