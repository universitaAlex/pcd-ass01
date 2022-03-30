package pcd.ass01.parallel;

import pcd.ass01.model.ConsoleSimulationDisplay;
import pcd.ass01.model.NoOpSimulationDisplay;
import pcd.ass01.model.SimulationDisplay;
import pcd.ass01.parallel.model.SimulationData;
import pcd.ass01.parallel.model.SimulationDataFactory;
import pcd.ass01.parallel.monitor.Flag;
import pcd.ass01.parallel.speed_test.SpeedTestUtils;
import pcd.ass01.ui.SimulationView;

import java.util.List;
import java.util.stream.Stream;

/**
 * Bodies simulation - legacy code: sequential, unstructured
 *
 * @author aricci
 */
public class ConsoleBodySimulationMain {

    public static void main(String[] args) {
        launchSimple();
    }

    private static void launchSimple() {
        int nWorkers = Runtime.getRuntime().availableProcessors();

        SimulationDataFactory dataFactory = new SimulationDataFactory();
        SimulationData simulationData = dataFactory.testBodySet4_many_bodies(2000, 1000);

        Flag runningFlag = new Flag();
        MasterAgent masterAgent = new MasterAgent(new NoOpSimulationDisplay(), simulationData, nWorkers, runningFlag);
        masterAgent.start();
        runningFlag.set();

        try {
            masterAgent.join();
        } catch (InterruptedException e) {
            System.out.println("Master agent interrupted");
        }
    }

    private static void launchSpeedTest() {
        List<Integer> nWorkersList = Stream.iterate(1, i -> i+1)
                .limit(Runtime.getRuntime().availableProcessors()+1)
                .toList();
        SimulationDataFactory dataFactory = new SimulationDataFactory();
        SimulationData simulationData = dataFactory.testBodySet4_many_bodies(2000, 1000);

        SimulationDisplay viewer = new ConsoleSimulationDisplay();
        for (int nWorkers: nWorkersList) {
            System.out.println("Test with " + nWorkers + " launched");
            SpeedTestUtils.test(5, () -> {
                Flag runningFlag = new Flag();
                MasterAgent masterAgent = new MasterAgent(viewer, simulationData, nWorkers, runningFlag);
                masterAgent.start();
                runningFlag.set();

                try {
                    masterAgent.join();
                } catch (InterruptedException e) {
                    System.out.println("Master agent interrupted");
                }
            });
        }
    }
}
