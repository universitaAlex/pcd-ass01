package pcd.ass01.model;

import java.util.List;

class RealSimulationData implements SimulationData {
    private final List<Body> bodies;
    private final Boundary bounds;
    private final double initialVt;
    private final double dt;
    private final long maxIterationsCount;

    RealSimulationData(List<Body> bodies, Boundary bounds, double initialVt, double dt, long maxIterationsCount) {
        this.bodies = bodies;
        this.bounds = bounds;
        this.initialVt = initialVt;
        this.dt = dt;
        this.maxIterationsCount = maxIterationsCount;
    }

    @Override
    public List<Body> getBodies() {
        return bodies;
    }

    @Override
    public Boundary getBounds() {
        return bounds;
    }

    @Override
    public double getVt(long iteration) {
        return initialVt + dt*iteration;
    }

    @Override
    public double getDt() {
        return dt;
    }

    @Override
    public long getMaxIterationsCount() {
        return maxIterationsCount;
    }

    @Override
    public boolean isOver(int currentIteration) {
        return currentIteration >= this.maxIterationsCount;
    }
}
