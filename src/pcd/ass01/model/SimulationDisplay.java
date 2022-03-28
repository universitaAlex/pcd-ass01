package pcd.ass01.model;

import java.util.Collection;

public interface SimulationDisplay {
    public void display(Collection<Body> bodies, double vt, long iter, Boundary bounds);
}
