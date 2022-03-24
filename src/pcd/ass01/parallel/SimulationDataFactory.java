package pcd.ass01.parallel;

import pcd.ass01.model.Body;
import pcd.ass01.model.Boundary;
import pcd.ass01.model.P2d;
import pcd.ass01.model.V2d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulationDataFactory {

    private final double dt=0.001;

    public SimulationData testBodySet1_two_bodies(long maxIteration) {
        Boundary bounds = new Boundary(-4.0, -4.0, 4.0, 4.0);
        List<Body> bodies = new ArrayList<>();
        bodies.add(new Body(0, new P2d(-0.1, 0), new V2d(0,0), 1));
        bodies.add(new Body(1, new P2d(0.1, 0), new V2d(0,0), 2));
        return new SimulationData(bodies,bounds,dt,maxIteration);
    }

    public SimulationData testBodySet2_three_bodies(long maxIteration) {
        Boundary bounds = new Boundary(-1.0, -1.0, 1.0, 1.0);
        List<Body> bodies = new ArrayList<Body>();
        bodies.add(new Body(0, new P2d(0, 0), new V2d(0,0), 10));
        bodies.add(new Body(1, new P2d(0.2, 0), new V2d(0,0), 1));
        bodies.add(new Body(2, new P2d(-0.2, 0), new V2d(0,0), 1));
        return new SimulationData(bodies,bounds,dt,maxIteration);
    }

    public SimulationData testBodySet3_some_bodies(long maxIteration) {
        Boundary bounds = new Boundary(-4.0, -4.0, 4.0, 4.0);
        int nBodies = 100;
        Random rand = new Random(System.currentTimeMillis());
        List<Body> bodies = new ArrayList<Body>();
        for (int i = 0; i < nBodies; i++) {
            double x = bounds.getX0()*0.25 + rand.nextDouble() * (bounds.getX1() - bounds.getX0()) * 0.25;
            double y = bounds.getY0()*0.25 + rand.nextDouble() * (bounds.getY1() - bounds.getY0()) * 0.25;
            Body b = new Body(i, new P2d(x, y), new V2d(0, 0), 10);
            bodies.add(b);
        }
        return new SimulationData(bodies,bounds,dt,maxIteration);
    }

    public SimulationData testBodySet4_many_bodies(long maxIteration) {
        Boundary bounds = new Boundary(-6.0, -6.0, 6.0, 6.0);
        int nBodies = 1000;
        Random rand = new Random(System.currentTimeMillis());
        List<Body> bodies = new ArrayList<Body>();
        for (int i = 0; i < nBodies; i++) {
            double x = bounds.getX0()*0.25 + rand.nextDouble() * (bounds.getX1() - bounds.getX0()) * 0.25;
            double y = bounds.getY0()*0.25 + rand.nextDouble() * (bounds.getY1() - bounds.getY0()) * 0.25;
            Body b = new Body(i, new P2d(x, y), new V2d(0, 0), 10);
            bodies.add(b);
        }
        return new SimulationData(bodies,bounds,dt,maxIteration);
    }
}
