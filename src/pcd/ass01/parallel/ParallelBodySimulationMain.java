package pcd.ass01.parallel;

import pcd.ass01.model.ConsoleSimulationDisplay;
import pcd.ass01.model.SimulationDisplay;
import pcd.ass01.parallel.model.MutableSimulationDataFactory;
import pcd.ass01.parallel.monitor.Flag;
import pcd.ass01.parallel.speed_test.SpeedTestUtils;
import pcd.ass01.ui.SimulationView;

/**
 * Bodies simulation - legacy code: sequential, unstructured
 *
 * @author aricci
 */
public class ParallelBodySimulationMain {

    public static void main(String[] args) {
        launchWithUI();
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
        int nWorkers = Runtime.getRuntime().availableProcessors();
        MutableSimulationDataFactory dataFactory = new MutableSimulationDataFactory();

        SimulationDisplay viewer = new ConsoleSimulationDisplay();
        SpeedTestUtils.test(10, () -> {

            Flag runningFlag = new Flag();
            MasterAgent masterAgent = new MasterAgent(viewer,dataFactory.testBodySet4_many_bodies(5000, 1000), nWorkers, runningFlag);
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
