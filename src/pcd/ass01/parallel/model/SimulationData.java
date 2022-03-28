package pcd.ass01.parallel.model;

import pcd.ass01.model.Body;
import pcd.ass01.model.Boundary;

import java.util.List;

public interface SimulationData {
    List<Body> getBodies();

    Boundary getBounds();

    double getVt();

    double getDt();

    long getMaxIterationsCount();

    long getCurrentIteration();

    boolean isOver();
}
