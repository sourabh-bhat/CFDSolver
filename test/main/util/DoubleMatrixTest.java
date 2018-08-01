package main.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class DoubleMatrixTest {

    private void assertMatrixEquals(double[][] expected, double[][] actual) {
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertArrayEquals(expected[i], actual[i], 1e-12);
        }
    }

    @Test
    public void add() {
        double[][] A = {
                {0.7145743975689531, 0.2671609402694344, 0.001845559237475758, 0.7076840591058451},
                {0.34608934560491844, 0.4736962459305115, 0.2535484676104236, 0.7542072165322476},
                {0.3254509941196886, 0.17868134542125047, 0.6412805512675501, 0.13652003090902365},
                {0.13093656578156743, 0.45716337776708826, 0.7153420151113619, 0.47145897592472696},
                {0.7430609571317965, 0.9080403201549879, 0.44606404707202785, 0.2877350173311791}
        };

        double[][] B = {
                {0.7293553154769675, 0.29233554044436927, 0.9027710169311947, 0.6314777116551691},
                {0.26133649807013004, 0.4453965492853025, 0.7984704293429422, 0.17527094878807425},
                {0.10340563959952231, 0.866242524623381, 0.4749205132040413, 0.06407316941803687},
                {0.8007985301923031, 0.721657001029854, 0.7928317867197299, 0.4733693589303589},
                {0.10172710511620353, 0.19395561292414498, 0.8241012391112054, 0.31841943000063433}
        };

        double[][] expectedMatrix = {
                {1.44392971304592, 0.5594964807138037, 0.9046165761686704, 1.339161770761014},
                {0.6074258436750485, 0.919092795215814, 1.052018896953365, 0.9294781653203219},
                {0.4288566337192109, 1.044923870044631, 1.116201064471591, 0.2005932003270605},
                {0.9317350959738705, 1.178820378796942, 1.508173801831091, 0.9448283348550859},
                {0.8447880622480001, 1.101995933079133, 1.270165286183233, 0.6061544473318135}
        };

        assertMatrixEquals(expectedMatrix, DoubleMatrix.add(A, B));

        // Exception on different size matrices
        A = new double[3][5];
        B = new double[4][5];
        try {
            DoubleMatrix.add(A, B);
            fail("Expected IllegalArgumentException not thrown.");
        } catch (IllegalArgumentException ex) {
            // OK expected exception.
        }
    }

    @Test
    public void subtract() {
        double[][] A = {
                {0.7145743975689531, 0.2671609402694344, 0.001845559237475758, 0.7076840591058451},
                {0.34608934560491844, 0.4736962459305115, 0.2535484676104236, 0.7542072165322476},
                {0.3254509941196886, 0.17868134542125047, 0.6412805512675501, 0.13652003090902365},
                {0.13093656578156743, 0.45716337776708826, 0.7153420151113619, 0.47145897592472696},
                {0.7430609571317965, 0.9080403201549879, 0.44606404707202785, 0.2877350173311791}
        };

        double[][] B = {
                {0.7293553154769675, 0.29233554044436927, 0.9027710169311947, 0.6314777116551691},
                {0.26133649807013004, 0.4453965492853025, 0.7984704293429422, 0.17527094878807425},
                {0.10340563959952231, 0.866242524623381, 0.4749205132040413, 0.06407316941803687},
                {0.8007985301923031, 0.721657001029854, 0.7928317867197299, 0.4733693589303589},
                {0.10172710511620353, 0.19395561292414498, 0.8241012391112054, 0.31841943000063433}
        };

        double[][] expectedMatrix = {
                {-0.01478091790801439, -0.02517460017493489, -0.9009254576937189, 0.076206347450676},
                {0.0847528475347884, 0.02829969664520903, -0.5449219617325186, 0.5789362677441734},
                {0.2220453545201662, -0.6875611792021306, 0.1663600380635088, 0.07244686149098678},
                {-0.6698619644107356, -0.2644936232627657, -0.07748977160836801, -0.001910383005631954},
                {0.641333852015593, 0.7140847072308429, -0.3780371920391775, -0.0306844126694552}
        };

        assertMatrixEquals(expectedMatrix, DoubleMatrix.subtract(A, B));

        // Exception on different size matrices
        A = new double[3][5];
        B = new double[4][5];
        try {
            DoubleMatrix.subtract(A, B);
            fail("Expected IllegalArgumentException not thrown.");
        } catch (IllegalArgumentException ex) {
            // OK expected exception.
        }
    }

    @Test
    public void multiply_matrix_scalar() {
        double[][] A = {
                {0.7145743975689531, 0.2671609402694344, 0.001845559237475758, 0.7076840591058451},
                {0.34608934560491844, 0.4736962459305115, 0.2535484676104236, 0.7542072165322476},
                {0.3254509941196886, 0.17868134542125047, 0.6412805512675501, 0.13652003090902365},
                {0.13093656578156743, 0.45716337776708826, 0.7153420151113619, 0.47145897592472696},
                {0.7430609571317965, 0.9080403201549879, 0.44606404707202785, 0.2877350173311791}
        };
        double scalar = 54.84;

        double[][] expectedMatrix = {
                {39.18725996268139, 14.65110596437578, 0.1012104685831705, 38.80939380136455},
                {18.97953971297373, 25.97750212682925, 13.90459796375563, 41.36072375462847},
                {17.84773251752372, 9.798884982901376, 35.16782543151245, 7.486758495050857},
                {7.180561267461158, 25.07083963674712, 39.22935610870709, 25.85481023971202},
                {40.74946288910772, 49.79693115729954, 24.46215234143001, 15.77938835044186}
        };

        assertMatrixEquals(expectedMatrix, DoubleMatrix.multiply(A, scalar));
    }

    @Test
    public void multiply_matrix_vector() {
        double[][] A = {
                {0.7145743975689531, 0.2671609402694344, 0.001845559237475758, 0.7076840591058451},
                {0.34608934560491844, 0.4736962459305115, 0.2535484676104236, 0.7542072165322476},
                {0.3254509941196886, 0.17868134542125047, 0.6412805512675501, 0.13652003090902365},
                {0.13093656578156743, 0.45716337776708826, 0.7153420151113619, 0.47145897592472696},
                {0.7430609571317965, 0.9080403201549879, 0.44606404707202785, 0.2877350173311791}
        };
        double[] v = {
                0.7293553154769675, 0.29233554044436927, 0.9027710169311947, 0.6314777116551691
        };

        double[] expectedVector = {
                1.047832100638582, 1.096061607063064, 0.9547431722890329, 1.172650257045931, 1.391799860244277
        };

        assertArrayEquals(expectedVector, DoubleMatrix.multiply(A, v), 1e-12);

        // Exception on different size matrices
        A = new double[3][5];
        v = new double[3];
        try {
            DoubleMatrix.multiply(A, v);
            fail("Expected IllegalArgumentException not thrown.");
        } catch (IllegalArgumentException ex) {
            // OK expected exception.
        }
    }

    @Test
    public void multiply_matrix_matrix() {
        double[][] A = {
                {0.7145743975689531, 0.2671609402694344, 0.001845559237475758, 0.7076840591058451},
                {0.34608934560491844, 0.4736962459305115, 0.2535484676104236, 0.7542072165322476},
                {0.3254509941196886, 0.17868134542125047, 0.6412805512675501, 0.13652003090902365},
                {0.13093656578156743, 0.45716337776708826, 0.7153420151113619, 0.47145897592472696},
                {0.7430609571317965, 0.9080403201549879, 0.44606404707202785, 0.2877350173311791}
        };

        double[][] B = {
                {0.7293553154769675, 0.29233554044436927, 0.9027710169311947, 0.6314777116551691, 0.26133649807013004},
                {0.4453965492853025, 0.7984704293429422, 0.17527094878807425, 0.10340563959952231, 0.866242524623381},
                {0.4749205132040413, 0.06407316941803687, 0.8007985301923031, 0.721657001029854, 0.7928317867197299},
                {0.4733693589303589, 0.10172710511620353, 0.19395561292414498, 0.8241012391112054, 0.31841943000063433}
        };

        double[][] expectedMatrix = {
                {0.9760436393948694, 0.4943245048810445, 0.8306598236117319, 1.063398924041698, 0.6449741107997576},
                {0.9408387321439053, 0.572375631472189, 0.744788584091657, 1.072048699719457, 0.9419571261654216},
                {0.686135155189051, 0.2927893277888242, 0.8651407230327586, 0.7992826339125266, 0.7917318385332738},
                {0.8620231012887126, 0.4971032371751834, 0.8626201445465512, 1.034718293996799, 1.147500448432066},
                {1.294443390869627, 1.000117658394092, 1.242982239276248, 1.122150949713216, 1.426046263421709}
        };

        assertMatrixEquals(expectedMatrix, DoubleMatrix.multiply(A, B));

        // Exception on different size matrices
        A = new double[3][5];
        B = new double[3][5];
        try {
            DoubleMatrix.multiply(A, B);
            fail("Expected IllegalArgumentException not thrown.");
        } catch (IllegalArgumentException ex) {
            // OK expected exception.
        }
    }

    @Test
    public void transpose() {
        double[][] A = {
                {0.7145743975689531, 0.2671609402694344, 0.001845559237475758, 0.7076840591058451},
                {0.34608934560491844, 0.4736962459305115, 0.2535484676104236, 0.7542072165322476},
                {0.3254509941196886, 0.17868134542125047, 0.6412805512675501, 0.13652003090902365},
                {0.13093656578156743, 0.45716337776708826, 0.7153420151113619, 0.47145897592472696},
                {0.7430609571317965, 0.9080403201549879, 0.44606404707202785, 0.2877350173311791}
        };

        double[][] expectedMatrix = {
                {0.7145743975689531, 0.3460893456049184, 0.3254509941196886, 0.1309365657815674, 0.7430609571317965},
                {0.2671609402694344, 0.4736962459305115, 0.1786813454212504, 0.4571633777670882, 0.9080403201549879},
                {0.001845559237475758, 0.2535484676104236, 0.6412805512675501, 0.7153420151113619, 0.4460640470720278},
                {0.7076840591058451, 0.7542072165322477, 0.1365200309090236, 0.4714589759247269, 0.2877350173311791}
        };

        assertMatrixEquals(expectedMatrix, DoubleMatrix.transpose(A));
    }

    @Test
    public void determinant() {

        double[][] A = new double[][]{
                {6}
        };
        double expectedValue = 6;
        assertEquals(expectedValue, DoubleMatrix.determinant(A), 1e-15);

        A = new double[][]{
                {1, 0},
                {1, 2}
        };
        expectedValue = 2;
        assertEquals(expectedValue, DoubleMatrix.determinant(A), 1e-15);

        A = new double[][]{
                {0, 2},
                {1, 2}
        };
        expectedValue = -2;
        assertEquals(expectedValue, DoubleMatrix.determinant(A), 1e-15);

        A = new double[][]{
                {0.7145743975689531, 0.2671609402694344, 0.001845559237475758, 0.7076840591058451},
                {0.34608934560491844, 0.4736962459305115, 0.2535484676104236, 0.7542072165322476},
                {0.3254509941196886, 0.17868134542125047, 0.6412805512675501, 0.13652003090902365},
                {0.13093656578156743, 0.45716337776708826, 0.7153420151113619, 0.47145897592472696}
        };
        expectedValue = 0.004445136217123793;
        assertEquals(expectedValue, DoubleMatrix.determinant(A), 1e-15);

        // not a square matrix exception
        A = new double[3][4];
        try {
            DoubleMatrix.determinant(A);
            fail("Expected IllegalArgumentException not thrown.");
        } catch (IllegalArgumentException ex) {
            // OK expected exception.
        }
    }

    @Test
    public void invert() {
        double[][] A = {
                {0.7145743975689531, 0.2671609402694344, 0.001845559237475758, 0.7076840591058451},
                {0.34608934560491844, 0.4736962459305115, 0.2535484676104236, 0.7542072165322476},
                {0.3254509941196886, 0.17868134542125047, 0.6412805512675501, 0.13652003090902365},
                {0.13093656578156743, 0.45716337776708826, 0.7153420151113619, 0.47145897592472696}
        };

        double[][] expectedMatrix = {
                {-7.488746692030849, 14.03215323391245, 10.30219807460469, -14.18986872044463},
                {-33.45753969619868, 56.54293604285976, 33.41089971800461, -49.90673880711576},
                {8.52812595981319, -15.32395500391962, -8.083127252965428, 14.05366696728109},
                {21.58318399597416, -35.47458758295441, -22.99452173205346, 33.13189124638124}
        };

        assertMatrixEquals(expectedMatrix, DoubleMatrix.invert(A));

        // not a square matrix exception
        A = new double[3][4];
        try {
            DoubleMatrix.invert(A);
            fail("Expected IllegalArgumentException not thrown.");
        } catch (IllegalArgumentException ex) {
            // OK expected exception.
        }

        // Singular matrix exception
        A = new double[][]{
                {1, 2, 4},
                {5, 6, 5},
                {2, 4, 8}
        };
        try {
            DoubleMatrix.invert(A);
            fail("Expected ArithmeticException not thrown.");
        } catch (ArithmeticException ex) {
            // OK expected exception.
        }
    }

    @Test
    public void cofactorMatrix() {
        double[][] A = {
                {0.7145743975689531, 0.2671609402694344, 0.001845559237475758, 0.7076840591058451},
                {0.34608934560491844, 0.4736962459305115, 0.2535484676104236, 0.7542072165322476},
                {0.3254509941196886, 0.17868134542125047, 0.6412805512675501, 0.13652003090902365},
                {0.13093656578156743, 0.45716337776708826, 0.7153420151113619, 0.47145897592472696}
        };

        double[][] expectedMatrix = {
                {-0.03328849914161233, -0.1487233214394297, 0.03790868156815922, 0.09594019286135139},
                {0.06237483254429501, 0.2513410528266302, -0.06811706737749848, -0.1576893740525206},
                {0.04579467377740831, 0.1485160003831934, -0.03593060169977698, -0.102213781346591},
                {-0.0630758993654805, -0.2218422521500477, 0.06247046401965749, 0.147275769721096}
        };

        assertMatrixEquals(expectedMatrix, DoubleMatrix.cofactorMatrix(A));

        // not a square matrix exception
        A = new double[3][4];
        try {
            DoubleMatrix.cofactorMatrix(A);
            fail("Expected IllegalArgumentException not thrown.");
        } catch (IllegalArgumentException ex) {
            // OK expected exception.
        }
    }

    @Test
    public void removeRowCol() {
        double[][] A = {
                {0.7145743975689531, 0.2671609402694344, 0.001845559237475758, 0.7076840591058451},
                {0.34608934560491844, 0.4736962459305115, 0.2535484676104236, 0.7542072165322476},
                {0.3254509941196886, 0.17868134542125047, 0.6412805512675501, 0.13652003090902365},
                {0.13093656578156743, 0.45716337776708826, 0.7153420151113619, 0.47145897592472696},
                {0.7430609571317965, 0.9080403201549879, 0.44606404707202785, 0.2877350173311791}
        };
        double[][] expectedMatrix = {
                {0.2671609402694344, 0.001845559237475758, 0.7076840591058451},
                {0.4736962459305115, 0.2535484676104236, 0.7542072165322476},
                {0.45716337776708826, 0.7153420151113619, 0.47145897592472696},
                {0.9080403201549879, 0.44606404707202785, 0.2877350173311791}
        };
        assertMatrixEquals(expectedMatrix, DoubleMatrix.removeRowColumn(A, 2, 0));
    }

    @Test
    public void removeColumn() {
        double[][] A = {
                {0.7145743975689531, 0.2671609402694344, 0.001845559237475758, 0.7076840591058451},
                {0.34608934560491844, 0.4736962459305115, 0.2535484676104236, 0.7542072165322476},
                {0.3254509941196886, 0.17868134542125047, 0.6412805512675501, 0.13652003090902365},
                {0.13093656578156743, 0.45716337776708826, 0.7153420151113619, 0.47145897592472696},
                {0.7430609571317965, 0.9080403201549879, 0.44606404707202785, 0.2877350173311791}
        };
        double[][] expectedMatrix = {
                {0.2671609402694344, 0.001845559237475758, 0.7076840591058451},
                {0.4736962459305115, 0.2535484676104236, 0.7542072165322476},
                {0.17868134542125047, 0.6412805512675501, 0.13652003090902365},
                {0.45716337776708826, 0.7153420151113619, 0.47145897592472696},
                {0.9080403201549879, 0.44606404707202785, 0.2877350173311791}
        };
        assertMatrixEquals(expectedMatrix, DoubleMatrix.removeColumn(A, 0));

        expectedMatrix = new double[][]{
                {0.7145743975689531, 0.001845559237475758, 0.7076840591058451},
                {0.34608934560491844, 0.2535484676104236, 0.7542072165322476},
                {0.3254509941196886, 0.6412805512675501, 0.13652003090902365},
                {0.13093656578156743, 0.7153420151113619, 0.47145897592472696},
                {0.7430609571317965, 0.44606404707202785, 0.2877350173311791}
        };
        assertMatrixEquals(expectedMatrix, DoubleMatrix.removeColumn(A, 1));

        expectedMatrix = new double[][]{
                {0.7145743975689531, 0.2671609402694344, 0.7076840591058451},
                {0.34608934560491844, 0.4736962459305115, 0.7542072165322476},
                {0.3254509941196886, 0.17868134542125047, 0.13652003090902365},
                {0.13093656578156743, 0.45716337776708826, 0.47145897592472696},
                {0.7430609571317965, 0.9080403201549879, 0.2877350173311791}
        };
        assertMatrixEquals(expectedMatrix, DoubleMatrix.removeColumn(A, 2));

        expectedMatrix = new double[][]{
                {0.7145743975689531, 0.2671609402694344, 0.001845559237475758},
                {0.34608934560491844, 0.4736962459305115, 0.2535484676104236},
                {0.3254509941196886, 0.17868134542125047, 0.6412805512675501},
                {0.13093656578156743, 0.45716337776708826, 0.7153420151113619},
                {0.7430609571317965, 0.9080403201549879, 0.44606404707202785}
        };
        assertMatrixEquals(expectedMatrix, DoubleMatrix.removeColumn(A, 3));
    }

    @Test
    public void removeRow() {
        double[][] A = {
                {0.7145743975689531, 0.2671609402694344, 0.001845559237475758, 0.7076840591058451},
                {0.34608934560491844, 0.4736962459305115, 0.2535484676104236, 0.7542072165322476},
                {0.3254509941196886, 0.17868134542125047, 0.6412805512675501, 0.13652003090902365},
                {0.13093656578156743, 0.45716337776708826, 0.7153420151113619, 0.47145897592472696},
                {0.7430609571317965, 0.9080403201549879, 0.44606404707202785, 0.2877350173311791}
        };
        double[][] expectedMatrix = {
                {0.34608934560491844, 0.4736962459305115, 0.2535484676104236, 0.7542072165322476},
                {0.3254509941196886, 0.17868134542125047, 0.6412805512675501, 0.13652003090902365},
                {0.13093656578156743, 0.45716337776708826, 0.7153420151113619, 0.47145897592472696},
                {0.7430609571317965, 0.9080403201549879, 0.44606404707202785, 0.2877350173311791}
        };
        assertMatrixEquals(expectedMatrix, DoubleMatrix.removeRow(A, 0));

        expectedMatrix = new double[][]{
                {0.7145743975689531, 0.2671609402694344, 0.001845559237475758, 0.7076840591058451},
                {0.3254509941196886, 0.17868134542125047, 0.6412805512675501, 0.13652003090902365},
                {0.13093656578156743, 0.45716337776708826, 0.7153420151113619, 0.47145897592472696},
                {0.7430609571317965, 0.9080403201549879, 0.44606404707202785, 0.2877350173311791}
        };
        assertMatrixEquals(expectedMatrix, DoubleMatrix.removeRow(A, 1));

        expectedMatrix = new double[][]{
                {0.7145743975689531, 0.2671609402694344, 0.001845559237475758, 0.7076840591058451},
                {0.34608934560491844, 0.4736962459305115, 0.2535484676104236, 0.7542072165322476},
                {0.13093656578156743, 0.45716337776708826, 0.7153420151113619, 0.47145897592472696},
                {0.7430609571317965, 0.9080403201549879, 0.44606404707202785, 0.2877350173311791}
        };
        assertMatrixEquals(expectedMatrix, DoubleMatrix.removeRow(A, 2));

        expectedMatrix = new double[][]{
                {0.7145743975689531, 0.2671609402694344, 0.001845559237475758, 0.7076840591058451},
                {0.34608934560491844, 0.4736962459305115, 0.2535484676104236, 0.7542072165322476},
                {0.3254509941196886, 0.17868134542125047, 0.6412805512675501, 0.13652003090902365},
                {0.7430609571317965, 0.9080403201549879, 0.44606404707202785, 0.2877350173311791}
        };
        assertMatrixEquals(expectedMatrix, DoubleMatrix.removeRow(A, 3));

        expectedMatrix = new double[][]{
                {0.7145743975689531, 0.2671609402694344, 0.001845559237475758, 0.7076840591058451},
                {0.34608934560491844, 0.4736962459305115, 0.2535484676104236, 0.7542072165322476},
                {0.3254509941196886, 0.17868134542125047, 0.6412805512675501, 0.13652003090902365},
                {0.13093656578156743, 0.45716337776708826, 0.7153420151113619, 0.47145897592472696}
        };
        assertMatrixEquals(expectedMatrix, DoubleMatrix.removeRow(A, 4));
    }
}