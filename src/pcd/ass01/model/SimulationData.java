package pcd.ass01.model;

import pcd.ass01.model.Body;
import pcd.ass01.model.Boundary;

import java.util.List;

public interface SimulationData {
    List<Body> getBodies();

    Boundary getBounds();

    double getVt(long iteration);

    double getDt();

    long getMaxIterationsCount();

    boolean isOver(int currentIteration);
}
