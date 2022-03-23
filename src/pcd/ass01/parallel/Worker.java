package pcd.ass01.parallel;

import pcd.ass01.model.Body;
import pcd.ass01.model.Boundary;
import pcd.ass01.model.V2d;
import pcd.ass01.parallel.monitor.Barrier;
import pcd.ass01.parallel.monitor.latch.Latch;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CyclicBarrier;

public class Worker extends Thread {

	private final SimulationData data;
	private final Iterable<Body> myBodies;
	private final CyclicBarrier barrier;
	private final Latch latch;

	public Worker(String name, SimulationData data, Iterable<Body> myBodies, CyclicBarrier barrier, Latch latch) {
		super(name);
		this.data = data;
		this.myBodies = myBodies;
		this.barrier = barrier;
		this.latch = latch;
	}

	public void run() {
		/* init virtual time */

		vt = 0;
		dt = 0.001;

		long iter = 0;

		/* simulation loop */

		while (iter < nSteps) {

			/* update bodies velocity */

			for (Body b : myBodies) {

				/* compute total force on bodies */
				V2d totalForce = computeTotalForceOnBody(b);

				/* compute instant acceleration */
				V2d acc = new V2d(totalForce).scalarMul(1.0 / b.getMass());

				/* update velocity */
				b.updateVelocity(acc, dt);
			}

			try {
				barrier.hitAndWaitAll();
			} catch (InterruptedException e) {
				e.printStackTrace();
				//TODO
			}
			log("force");
			/* compute bodies new pos and check collisions with boundaries */

			for (Body b : myBodies) {
				b.updatePos(dt);
				b.checkAndSolveBoundaryCollision(bounds);
			}

			try {
				barrier.hitAndWaitAll();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			/* update virtual time */
			log("collision");
			vt = vt + dt;
			iter++;

			/* display current stage */
			try {
				barrier.hitAndWaitAll();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			log("display");
			//viewer.display(bodies, vt, iter, bounds);
		}
	}

	private V2d computeTotalForceOnBody(Body b) {

		V2d totalForce = new V2d(0, 0);

		/* compute total repulsive force */

		for (int j = 0; j < bodies.size(); j++) {
			Body otherBody = bodies.get(j);
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
