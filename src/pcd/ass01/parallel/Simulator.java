package pcd.ass01.parallel;

import pcd.ass01.model.*;
import pcd.ass01.parallel.monitor.CyclicBarrier;
import pcd.ass01.parallel.monitor.IterationTracker;
import pcd.ass01.parallel.monitor.RealCyclicBarrier;
import pcd.ass01.parallel.monitor.latch.Latch;
import pcd.ass01.parallel.monitor.latch.RealLatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Simulator {

	private SimulationDisplay viewer;

	/* bodies in the field */
	ArrayList<Body> bodies;

	/* boundary of the field */
	private Boundary bounds;

	/* virtual time step */
	double dt;

	private SimulationData simulationData;
	private boolean isRunning = false;
	IterationTracker iterationTracker = new IterationTracker();
	Latch latch;

	public Simulator(SimulationDisplay viewer) {
		this.viewer = viewer;

		/* initializing boundary and bodies */

		// testBodySet1_two_bodies();
		// testBodySet2_three_bodies();
		// testBodySet3_some_bodies();
		testBodySet4_many_bodies();
	}
	
	public void configure(long nSteps) {
		dt = 0.001;
		int nWorkers = Runtime.getRuntime().availableProcessors();

		int partitionSize = nWorkers > bodies.size() ? 1: (int) Math.ceil(bodies.size()/(double) nWorkers);

		Partitions<Body> partitions = Partitions.ofSize(bodies, partitionSize);
		List<Worker> workers = new ArrayList<>();

		CyclicBarrier endForceComputationBarrier = new RealCyclicBarrier(partitions.size() + 1);
		latch = new RealLatch(partitions.size());

		simulationData = new SimulationData(bodies, bounds, dt, nSteps);


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

		/* simulation loop */

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
	public synchronized void playSimulation() {
		if (isRunning) return;
		isRunning = true;
		iterationTracker.setCurrentIteration(simulationData.getCurrentIteration());
	}
	public synchronized void pauseSimulation() {
		if (!isRunning) return;
		isRunning = false;
	}
	private void testBodySet1_two_bodies() {
		bounds = new Boundary(-4.0, -4.0, 4.0, 4.0);
		bodies = new ArrayList<Body>();
		bodies.add(new Body(0, new P2d(-0.1, 0), new V2d(0,0), 1));
		bodies.add(new Body(1, new P2d(0.1, 0), new V2d(0,0), 2));		
	}

	private void testBodySet2_three_bodies() {
		bounds = new Boundary(-1.0, -1.0, 1.0, 1.0);
		bodies = new ArrayList<Body>();
		bodies.add(new Body(0, new P2d(0, 0), new V2d(0,0), 10));
		bodies.add(new Body(1, new P2d(0.2, 0), new V2d(0,0), 1));		
		bodies.add(new Body(2, new P2d(-0.2, 0), new V2d(0,0), 1));		
	}

	private void testBodySet3_some_bodies() {
		bounds = new Boundary(-4.0, -4.0, 4.0, 4.0);
		int nBodies = 100;
		Random rand = new Random(System.currentTimeMillis());
		bodies = new ArrayList<Body>();
		for (int i = 0; i < nBodies; i++) {
			double x = bounds.getX0()*0.25 + rand.nextDouble() * (bounds.getX1() - bounds.getX0()) * 0.25;
			double y = bounds.getY0()*0.25 + rand.nextDouble() * (bounds.getY1() - bounds.getY0()) * 0.25;
			Body b = new Body(i, new P2d(x, y), new V2d(0, 0), 10);
			bodies.add(b);
		}
	}

	private void testBodySet4_many_bodies() {
		bounds = new Boundary(-6.0, -6.0, 6.0, 6.0);
		int nBodies = 1000;
		Random rand = new Random(System.currentTimeMillis());
		bodies = new ArrayList<Body>();
		for (int i = 0; i < nBodies; i++) {
			double x = bounds.getX0()*0.25 + rand.nextDouble() * (bounds.getX1() - bounds.getX0()) * 0.25;
			double y = bounds.getY0()*0.25 + rand.nextDouble() * (bounds.getY1() - bounds.getY0()) * 0.25;
			Body b = new Body(i, new P2d(x, y), new V2d(0, 0), 10);
			bodies.add(b);
		}
	}
	
	

}
