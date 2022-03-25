package pcd.ass01.parallel;

import pcd.ass01.model.*;
import pcd.ass01.parallel.monitor.*;
import pcd.ass01.parallel.monitor.latch.Latch;
import pcd.ass01.parallel.monitor.latch.RealLatch;

import java.util.ArrayList;
import java.util.List;

public class Simulator {

	private final SimulationDisplay viewer;
	private final SimulationData simulationData;
	private boolean isRunning = false;
	private final IterationTracker iterationTracker = new IterationTracker();

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
				if(isRunning) {
					iterationTracker.setCurrentIteration(simulationData.getCurrentIteration());
				} else {
					iterationTracker.waitIteration(simulationData.getCurrentIteration());
				}
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
		}
	}

	public synchronized void playSimulation() {
		if (isRunning) return;
		isRunning = true;
		iterationTracker.setCurrentIteration(simulationData.getCurrentIteration());
	}
	public synchronized void pauseSimulation() {
		if (!isRunning) return;
		isRunning = false;
	}

}
