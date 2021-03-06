package main.geom.factory;

import main.geom.*;
import main.geom.GeometryHelper.TriGeom;

public class Hexahedron implements Geometry {
    private final Point[] points;
    private final VTKType vtkType;
    private final double volume;
    private final Point centroid;

    public Hexahedron(Point p0, Point p1, Point p2, Point p3, Point p4, Point p5, Point p6, Point p7) {
        this.points = new Point[]{p0, p1, p2, p3, p4, p5, p6, p7};
        this.vtkType = VTKType.VTK_HEXAHEDRON;

        TriGeom[] surfaceTriangles = {
                new TriGeom(p0, p3, p2),
                new TriGeom(p0, p2, p1),
                new TriGeom(p4, p5, p6),
                new TriGeom(p4, p6, p7),
                new TriGeom(p1, p2, p6),
                new TriGeom(p1, p6, p5),
                new TriGeom(p0, p4, p7),
                new TriGeom(p0, p7, p3),
                new TriGeom(p0, p1, p5),
                new TriGeom(p0, p5, p4),
                new TriGeom(p3, p7, p6),
                new TriGeom(p3, p6, p2)
        };
        volume = GeometryHelper.volume(surfaceTriangles);
        centroid = GeometryHelper.centroid(surfaceTriangles);

        // The algorithm below is probably less expensive, but may fail in case of non-convex geometry.
//        Pyramid pyr1 = new Pyramid(p0, p1, p2, p3, p6);
//        Pyramid pyr2 = new Pyramid(p0, p1, p5, p4, p6);
//        Pyramid pyr3 = new Pyramid(p0, p3, p7, p4, p6);
//
//        volume = pyr1.volume() + pyr2.volume() + pyr3.volume();
    }

    @Override
    public Point[] points() {
        return points;
    }

    @Override
    public VTKType vtkType() {
        return vtkType;
    }

    @Override
    public double length() {
        throw new ArithmeticException("Cannot calculate length of a hexahedron.");
    }

    @Override
    public double area() {
        throw new ArithmeticException("Cannot calculate area of a hexahedron.");
    }

    @Override
    public double volume() {
        return volume;
    }

    @Override
    public Point centroid() {
        return centroid;
    }

    @Override
    public Vector unitNormal() {
        throw new ArithmeticException("Cannot calculate normal of a hexahedron.");
    }
}
