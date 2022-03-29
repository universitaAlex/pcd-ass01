package pcd.ass01.parallel;

import pcd.ass01.model.Body;
import pcd.ass01.model.V2d;
import pcd.ass01.parallel.model.SimulationData;
import pcd.ass01.parallel.monitor.CyclicBarrier;
import pcd.ass01.parallel.monitor.IterationTracker;
import pcd.ass01.parallel.monitor.latch.Latch;

import java.util.concurrent.BrokenBarrierException;

public class WorkerAgent extends Thread {

    private final IterationTracker iterationTracker;
    private final SimulationData data;
    private final Iterable<Body> myBodies;
    private final CyclicBarrier endForceComputationBarrier;
    private final Latch latch;

    public WorkerAgent(SimulationData data, Iterable<Body> myBodies, CyclicBarrier endForceComputationBarrier, Latch latch, IterationTracker iterationTracker) {
        super();
        this.data = data;
        this.myBodies = myBodies;
        this.endForceComputationBarrier = endForceComputationBarrier;
        this.latch = latch;
        this.iterationTracker = iterationTracker;
    }

    public void run() {
        /* simulation loop */
        int iteration = 0;
        while (true) {
            boolean shouldContinue = iterationTracker.waitIteration(iteration);
            if (!shouldContinue) {
                break;
            }

            /* update bodies velocity */
            for (Body b : myBodies) {
                /* compute total force on bodies */
                V2d totalForce = computeTotalForceOnBody(b);
                /* compute instant acceleration */
                V2d acc = new V2d(totalForce).scalarMul(1.0 / b.getMass());
                /* update velocity */
                b.updateVelocity(acc, data.getDt());
            }
            try {
                endForceComputationBarrier.hitAndWaitAll();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            /* compute bodies new pos and check collisions with boundaries */
            for (Body b : myBodies) {
                b.updatePos(data.getDt());
                b.checkAndSolveBoundaryCollision(data.getBounds());
            }
            latch.countDown();
            iteration++;
        }

    }

    private V2d computeTotalForceOnBody(Body b) {
        V2d totalForce = new V2d(0, 0);
        /* compute total repulsive force */
        for (Body otherBody: data.getBodies()) {
            if (!b.equals(otherBody)) {
                try {
                    V2d forceByOtherBody = b.computeRepulsiveForceBy(otherBody);
                    totalForce.sum(forceByOtherBody);
                } catch (Exception ex) {
                }
            }
        }
        /* add friction force */
        totalForce.sum(b.getCurrentFrictionForce());
        return totalForce;
    }
}
