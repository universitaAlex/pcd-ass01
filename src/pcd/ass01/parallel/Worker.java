package pcd.ass01.parallel;

import pcd.ass01.model.Body;
import pcd.ass01.model.V2d;
import pcd.ass01.parallel.monitor.*;

public class Worker extends Thread {

	private final SimulationData data;
	private final TaskBag taskBag;
	private final TaskCompletionLatch taskCompletionLatch;

	public Worker(SimulationData data, TaskBag taskBag, TaskCompletionLatch taskCompletionLatch) {
		super();
		this.data = data;
		this.taskBag = taskBag;
		this.taskCompletionLatch = taskCompletionLatch;
	}

	public void run() {
		/* simulation loop */
		while (true) {
			Task task = taskBag.getATask();
			Body b = task.getBody();
			switch (task.getTaskType()) {
				case COMPUTE_VELOCITY -> {
					/* compute total force on bodies */
					V2d totalForce = computeTotalForceOnBody(b);
					/* compute instant acceleration */
					V2d acc = new V2d(totalForce).scalarMul(1.0 / b.getMass());
					/* update velocity */
					b.updateVelocity(acc, data.getDt());
				}
				case COMPUTE_POSITION -> {
					b.updatePos(data.getDt());
					b.checkAndSolveBoundaryCollision(data.getBounds());
				}
			}
			taskCompletionLatch.notifyCompletion();
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
					ex.printStackTrace();
				}
			}
		}
		/* add friction force */
		totalForce.sum(b.getCurrentFrictionForce());
		return totalForce;
	}
}
