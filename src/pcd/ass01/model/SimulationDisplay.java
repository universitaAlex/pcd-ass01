package pcd.ass01.model;

import pcd.ass01.parallel.SimulationData;

import java.util.Collection;

public interface SimulationDisplay {
    public void display(Collection<Body> bodies, double vt, long iter, Boundary bounds);
    public default void display(SimulationData simulationData) {
        display(
                simulationData.getBodies(),
                simulationData.getVt(),
                simulationData.getIterationsCount(),
                simulationData.getBounds()
        );
    }
}
