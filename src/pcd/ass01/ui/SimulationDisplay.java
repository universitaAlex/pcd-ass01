package pcd.ass01.ui;

import pcd.ass01.model.Body;
import pcd.ass01.model.Boundary;

import java.util.Collection;

public interface SimulationDisplay {
    public void display(Collection<Body> bodies, double vt, long iter, Boundary bounds);
}
