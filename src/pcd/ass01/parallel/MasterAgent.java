package pcd.ass01.parallel;

import pcd.ass01.model.Body;
import pcd.ass01.model.SimulationDisplay;
import pcd.ass01.parallel.model.SimulationData;
import pcd.ass01.parallel.monitor.CyclicBarrier;
import pcd.ass01.parallel.monitor.Flag;
import pcd.ass01.parallel.monitor.IterationTracker;
import pcd.ass01.parallel.monitor.RealCyclicBarrier;
import pcd.ass01.parallel.monitor.latch.Latch;
import pcd.ass01.parallel.monitor.latch.RealLatch;

import java.util.List;

public class MasterAgent extends Thread {

    private final int nWorkers;
    private final SimulationDisplay viewer;
    private final SimulationData simulationData;
    private final IterationTracker iterationTracker = new IterationTracker();
    private final Flag isRunningFlag;

    public MasterAgent(
            SimulationDisplay viewer,
            SimulationData simulationData,
            int nWorkers,
            Flag isRunningFlag
    ) {
        this.viewer = viewer;
        this.simulationData = simulationData;
        this.nWorkers = nWorkers;
        this.isRunningFlag = isRunningFlag;
    }

    @Override
    public void run() {
        configure();
    }

    private void configure() {
        int partitionSize = nWorkers > simulationData.getBodies().size() ? 1 : (int) Math.ceil(simulationData.getBodies().size() / (double) nWorkers);

        Partitions<Body> partitions = Partitions.ofSize(simulationData.getBodies(), partitionSize);

        System.out.println("Number of partitions " + partitions.size());
        CyclicBarrier endForceComputationBarrier = new RealCyclicBarrier(partitions.size());
        Latch latch = new RealLatch(partitions.size());

        /* display initial stage */
        viewer.display(
                simulationData.getBodies(),
                simulationData.getVt(0),
                0,
                simulationData.getBounds()
        );
        for (List<Body> partition : partitions) {
            WorkerAgent worker = new WorkerAgent(simulationData, partition, endForceComputationBarrier, latch, iterationTracker);
            worker.start();
        }
        simulationLoop(latch);
    }

    private void simulationLoop(Latch latch) {
        long currentIteration = 0;
        while (currentIteration < simulationData.getMaxIterationsCount()) {
            try {
                isRunningFlag.awaitSet();

                iterationTracker.setCurrentIteration(currentIteration);

                latch.await();
                latch.resetCount();

                /* update virtual time */
                currentIteration++;

                /* display current stage */
                viewer.display(
                        simulationData.getBodies(),
                        simulationData.getVt(currentIteration),
                        currentIteration,
                        simulationData.getBounds()
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        iterationTracker.terminate();
    }

}
