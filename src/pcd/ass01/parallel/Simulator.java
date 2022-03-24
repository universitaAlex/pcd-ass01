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

	private SimulationDisplay viewer;
	private SimulationData simulationData;
	private boolean isRunning = false;
	private IterationTracker iterationTracker = new IterationTracker();

	public Simulator(SimulationDisplay viewer, SimulationData simulationData) {
		this.viewer = viewer;
		this.simulationData=simulationData;
	}
	
	public void configure() {
		int nWorkers = Runtime.getRuntime().availableProcessors();
		int partitionSize = nWorkers > simulationData.getBodies().size() ? 1: (int) Math.ceil(simulationData.getBodies().size()/(double) nWorkers);

		Partitions<Body> partitions = Partitions.ofSize(simulationData.getBodies(), partitionSize);
		List<Worker> workers = new ArrayList<>();

		CyclicBarrier endForceComputationBarrier = new RealCyclicBarrier(partitions.size() + 1);
		Latch latch = new RealLatch(partitions.size());

		/* display current stage */
		viewer.display(
				simulationData.getBodies(),
				simulationData.getVt(),
				simulationData.getCurrentIteration(),
				simulationData.getBounds()
		);
		for (List<Body> partition : partitions) {
			Worker worker = new Worker("Worker", simulationData, partition, endForceComputationBarrier, latch, iterationTracker);
			workers.add(worker);
			worker.start();
		}
		simulationLoop(endForceComputationBarrier,latch);
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

	private void simulationLoop(CyclicBarrier endForceComputationBarrier, Latch latch) {
		while (!simulationData.isOver()) {
			try {
				if(isRunning) {
					iterationTracker.setCurrentIteration(simulationData.getCurrentIteration());
				} else {
					iterationTracker.waitIteration(simulationData.getCurrentIteration());
				}
				endForceComputationBarrier.hitAndWaitAll();
				endForceComputationBarrier.reset();
				latch.await();
				latch.resetCount();

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

}
