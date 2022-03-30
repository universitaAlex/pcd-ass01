package pcd.ass01.model;

import java.util.Collection;

public class NoOpSimulationDisplay implements SimulationDisplay {
    @Override
    public void display(Collection<Body> bodies, double vt, long iter, Boundary bounds) {
    }
}
