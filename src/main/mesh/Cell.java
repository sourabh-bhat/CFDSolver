package main.mesh;

import main.geom.VTKType;

import java.util.ArrayList;

public class Cell {
    public final int index;
    public final Node[] nodes;
    public final ArrayList<Face> faces;
    public final VTKType vtkType;
    public final Shape shape;

    public final double[] U;
    public final double[] residual;

    /**
     * Local time step or pseudo-time step
     */
    public double dt;

    public Cell(int index, Node[] nodes, VTKType vtkType, Shape shape, int numVars) {
        this.index = index;
        this.nodes = nodes;
        this.faces = new ArrayList<>();
        this.vtkType = vtkType;
        this.shape = shape;

        this.U = new double[numVars];
        this.residual = new double[numVars];
    }

    @Override
    public String toString() {
        return "Cell{" +
                "vtkType=" + vtkType +
                ", shape=" + shape +
                '}';
    }
}
