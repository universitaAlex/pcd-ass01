package pcd.ass01.parallel;

import pcd.ass01.model.Body;
import pcd.ass01.model.Boundary;

import java.util.List;

public class SimulationData {
    private final List<Body> bodies;
    private final Boundary bounds;
    private double vt = 0;
    private final double dt;
    private final int maxIterationsCount;
    private int iterationsCount = 0;

    public SimulationData(List<Body> bodies, Boundary bounds, double dt, int maxIterationsCount) {
        this.bodies = bodies;
        this.bounds = bounds;
        this.dt = dt;
        this.maxIterationsCount = maxIterationsCount;
    }

    public List<Body> getBodies() {
        return bodies;
    }

    public Boundary getBounds() {
        return bounds;
    }

    public double getVt() {
        return vt;
    }

    public double getDt() {
        return dt;
    }

    public int getMaxIterationsCount() {
        return maxIterationsCount;
    }

    public int getIterationsCount() {
        return iterationsCount;
    }

    public void nextIteration() {
        if(!isOver()){
            this.vt += this.dt;
            this.iterationsCount++;
        } else {
            throw new IllegalStateException("Max iterations count reached");
        }

    }

    public boolean isOver() {
        return this.iterationsCount == this.maxIterationsCount;
    }
}