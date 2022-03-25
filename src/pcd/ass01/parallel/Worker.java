package pcd.ass01.parallel;

import pcd.ass01.model.Body;
import pcd.ass01.model.V2d;
import pcd.ass01.parallel.monitor.CyclicBarrier;
import pcd.ass01.parallel.monitor.IterationTracker;
import pcd.ass01.parallel.monitor.latch.Latch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;

public class Worker extends Thread {

	private final IterationTracker iterationTracker;
	private final SimulationData data;
	private List<Body> myBodies;
	private final CyclicBarrier endForceComputationBarrier;
	private final Latch latch;

	public Worker(String name, SimulationData data, List<Body> myBodies, CyclicBarrier endForceComputationBarrier, Latch latch, IterationTracker iterationTracker) {
		super(name);
		this.data = data;
		this.myBodies = myBodies;
		this.endForceComputationBarrier = endForceComputationBarrier;
		this.latch = latch;
		this.iterationTracker = iterationTracker;
	}

	public void run() {
		/* simulation loop */
		int iteration = 0;
		while (!data.isOver()) {
			iterationTracker.waitIteration(iteration);
			/* update bodies velocity */
			List<Body> newBodies = new ArrayList<>(myBodies.size());
			for (Body oldBody : myBodies) {
				Body b = new Body(oldBody);
				/* compute total force on bodies */
				V2d totalForce = computeTotalForceOnBody(b);
				/* compute instant acceleration */
				V2d acc = new V2d(totalForce).scalarMul(1.0 / b.getMass());
				/* update velocity */
				b.updateVelocity(acc, data.getDt());
				b.updatePos(data.getDt());
				b.checkAndSolveBoundaryCollision(data.getBounds());
				newBodies.add(b);
			}
			iterationTracker.addResults(newBodies);
			this.myBodies = newBodies;
			latch.countDown();
			iteration++;
		}
	}

	private V2d computeTotalForceOnBody(Body b) {
		V2d totalForce = new V2d(0, 0);
		/* compute total repulsive force */
		for (int j = 0; j < data.getBodies().size(); j++) {
			Body otherBody = data.getBodies().get(j);
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
	
	private void log(String msg) {
		synchronized(System.out) {
			System.out.println("[ "+getName()+" ] "+msg);
		}
	}
	
	private void waitFor(long ms) throws InterruptedException{
		Thread.sleep(ms);
	}
}
