package pcd.ass01.parallel;

import pcd.ass01.model.Body;
import pcd.ass01.model.Boundary;
import pcd.ass01.model.V2d;
import pcd.ass01.parallel.monitor.Barrier;
import pcd.ass01.parallel.monitor.latch.Latch;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Worker extends Thread {

	private final SimulationData data;
	private final Iterable<Body> myBodies;
	private final CyclicBarrier endForceComputationBarrier;
	private final CyclicBarrier endIterationBarrier;
	private final Latch latch;

	public Worker(String name, SimulationData data, Iterable<Body> myBodies, CyclicBarrier endForceComputationBarrier,CyclicBarrier endIterationBarrier, Latch latch) {
		super(name);
		this.data = data;
		this.myBodies = myBodies;
		this.endForceComputationBarrier = endForceComputationBarrier;
		this.endIterationBarrier = endIterationBarrier;
		this.latch = latch;
	}

	public void run() {

		/* simulation loop */

		while (!data.isOver()) {

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
				endForceComputationBarrier.await();
			} catch (BrokenBarrierException | InterruptedException e) {
				e.printStackTrace();
				//TODO
			}
			/* compute bodies new pos and check collisions with boundaries */

			for (Body b : myBodies) {
				b.updatePos(data.getDt());
				b.checkAndSolveBoundaryCollision(data.getBounds());
			}

			try {
				latch.countDown();
				endIterationBarrier.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (BrokenBarrierException e) {
				e.printStackTrace();
			}
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
