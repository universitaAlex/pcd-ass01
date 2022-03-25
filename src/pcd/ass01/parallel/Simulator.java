package pcd.ass01.parallel;

import pcd.ass01.model.*;
import pcd.ass01.parallel.monitor.CyclicBarrier;
import pcd.ass01.parallel.monitor.IterationTracker;
import pcd.ass01.parallel.monitor.RealCyclicBarrier;
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
		int partitionSize = nWorkers > simulationData.getBodies().size() ? 1: (int) Math.ceil(simulationData.getBodies().size()/(double) nWorkers);

		Partitions<Body> partitions = Partitions.ofSize(simulationData.getBodies(), partitionSize);

		System.out.println("Number of partitions " + partitions.size());
		CyclicBarrier endForceComputationBarrier = new RealCyclicBarrier(partitions.size() + 1);
		Latch latch = new RealLatch(partitions.size());

		/* display initial stage */
		viewer.display(
				simulationData.getBodies(),
				simulationData.getVt(),
				simulationData.getCurrentIteration(),
				simulationData.getBounds()
		);
		for (List<Body> partition : partitions) {
			Worker worker = new Worker("Worker", simulationData, partition, endForceComputationBarrier, latch, iterationTracker);
			worker.start();
		}
		simulationLoop(endForceComputationBarrier,latch);
	}

	private void simulationLoop(CyclicBarrier endForceComputationBarrier, Latch latch) {
		while (!simulationData.isOver()) {
			try {
				if(isRunning) {
					iterationTracker.setCurrentIteration(simulationData.getCurrentIteration());
				} else {
					iterationTracker.waitIteration(simulationData.getCurrentIteration());
				}
				latch.await();
				latch.resetCount();
				List<Body> results = iterationTracker.getResults();
				iterationTracker.clearResults();
				/* update virtual time */
				simulationData.nextIteration();

				simulationData.setBodies(results);

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
