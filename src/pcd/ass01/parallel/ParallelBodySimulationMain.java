package pcd.ass01.parallel;

import pcd.ass01.model.ConsoleSimulationDisplay;
import pcd.ass01.model.SimulationDisplay;
import pcd.ass01.parallel.model.MutableSimulationDataFactory;
import pcd.ass01.parallel.monitor.Flag;
import pcd.ass01.parallel.speed_test.SpeedTestUtils;
import pcd.ass01.ui.SimulationView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Bodies simulation - legacy code: sequential, unstructured
 *
 * @author aricci
 */
public class ParallelBodySimulationMain {

    public static void main(String[] args) {
        launchSpeedTest();
    }

    private static void launchWithUI() {
        int nWorkers = Runtime.getRuntime().availableProcessors();
        Flag runningFlag = new Flag();
        SimulationView viewer = new SimulationView(620, 620);
        MutableSimulationDataFactory dataFactory = new MutableSimulationDataFactory();
        MasterAgent masterAgent = new MasterAgent(viewer,dataFactory.testBodySet4_many_bodies(5000, 1000), nWorkers, runningFlag);
        masterAgent.start();
        Controller controller = new Controller(runningFlag);
        viewer.addListener(controller);
    }

    private static void launchSpeedTest() {
        List<Integer> nWorkersList = Stream.iterate(1, i -> i+1)
                .limit(Runtime.getRuntime().availableProcessors()+1)
                .toList();
        MutableSimulationDataFactory dataFactory = new MutableSimulationDataFactory();

        SimulationDisplay viewer = new ConsoleSimulationDisplay();
        for (int nWorkers: nWorkersList) {
            System.out.println("Test with " + nWorkers + " launched");
            SpeedTestUtils.test(5, () -> {
                Flag runningFlag = new Flag();
                MasterAgent masterAgent = new MasterAgent(viewer,dataFactory.testBodySet4_many_bodies(2000, 1000), nWorkers, runningFlag);
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
