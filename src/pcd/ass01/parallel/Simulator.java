package pcd.ass01.parallel;

import pcd.ass01.model.*;
import pcd.ass01.parallel.monitor.*;

public class Simulator {

	private final SimulationDisplay viewer;
	private final SimulationData simulationData;
	private final Flag runningFlag = new Flag();

	public Simulator(SimulationDisplay viewer, SimulationData simulationData) {
		this.viewer = viewer;
		this.simulationData = simulationData;
	}
	
	public void configure() {
		int nWorkers = Runtime.getRuntime().availableProcessors();

		TaskBag bag = new TaskBag();
		TaskCompletionLatch taskLatch = new TaskCompletionLatch(nWorkers);

		/* display initial stage */
		viewer.display(
				simulationData.getBodies(),
				simulationData.getVt(),
				simulationData.getCurrentIteration(),
				simulationData.getBounds()
		);
		for (int i = 0; i < nWorkers; i++) {
			Worker worker = new Worker(simulationData, bag, taskLatch);
			worker.start();
		}
		simulationLoop(bag, taskLatch);
	}

	private void simulationLoop(TaskBag taskBag, TaskCompletionLatch taskCompletionLatch) {
		while (!simulationData.isOver()) {
			try {
				runningFlag.waitSet();
				for (Body body: simulationData.getBodies()) {
					taskBag.addNewTask(new Task(Task.TaskType.COMPUTE_FORCES, body));
				}
				taskCompletionLatch.waitCompletion();

				for (Body body: simulationData.getBodies()) {
					taskBag.addNewTask(new Task(Task.TaskType.COMPUTE_POSITIONS, body));
				}
				taskCompletionLatch.waitCompletion();
				/* update virtual time */
				simulationData.nextIteration();

				/* display current stage */
				viewer.display(
						simulationData.getBodies(),
						simulationData.getVt(),
						simulationData.getCurrentIteration(),
						simulationData.getBounds()
				);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
	}

	public synchronized void playSimulation() {
		if (runningFlag.isSet()) return;
		runningFlag.set();
	}
	public synchronized void pauseSimulation() {
		if (runningFlag.isSet()) return;
		runningFlag.reset();
	}

}
