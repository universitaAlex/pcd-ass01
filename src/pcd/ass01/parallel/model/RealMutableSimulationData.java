package pcd.ass01.parallel.model;

import pcd.ass01.model.Body;
import pcd.ass01.model.Boundary;

import java.util.List;

class RealMutableSimulationData implements SimulationData, MutableSimulationData {
    private final List<Body> bodies;
    private final Boundary bounds;
    private double vt = 0;
    private final double dt;
    private final long maxIterationsCount;
    private long iterationsCount = 0;

    RealMutableSimulationData(List<Body> bodies, Boundary bounds, double dt, long maxIterationsCount) {
        this.bodies = bodies;
        this.bounds = bounds;
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
    public double getVt() {
        return vt;
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
    public long getCurrentIteration() {
        return iterationsCount;
    }

    public void nextIteration() {
        if (!isOver()) {
            this.vt += this.dt;
            this.iterationsCount++;
        } else {
            throw new IllegalStateException("Max iterations count reached");
        }

    }

    @Override
    public boolean isOver() {
        return this.iterationsCount == this.maxIterationsCount;
    }
}
