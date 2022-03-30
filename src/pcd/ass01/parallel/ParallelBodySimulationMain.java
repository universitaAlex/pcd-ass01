package pcd.ass01.parallel;

import gov.nasa.jpf.vm.Verify;
import pcd.ass01.model.ConsoleSimulationDisplay;
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
public class ParallelBodySimulationMain {

    public static void main(String[] args) {
        launchJPFTest();
    }

    private static void launchWithUI() {
        int nWorkers = Runtime.getRuntime().availableProcessors();
        Flag runningFlag = new Flag();
        SimulationView viewer = new SimulationView(620, 620);
        SimulationDataFactory dataFactory = new SimulationDataFactory();
        MasterAgent masterAgent = new MasterAgent(viewer,dataFactory.testBodySet4_many_bodies(5000, 1000), nWorkers, runningFlag);
        masterAgent.start();
        Controller controller = new Controller(runningFlag);
        viewer.addListener(controller);
    }
    private static void launchJPFTest() {
        Verify.beginAtomic();
        MutableSimulationDataFactory dataFactory = new MutableSimulationDataFactory();
        int nWorkers = 2;
        SimulationDisplay viewer = new ConsoleSimulationDisplay();

        Flag runningFlag = new Flag();
        MasterAgent masterAgent = new MasterAgent(viewer,dataFactory.testBodySet4_many_bodies(2, 5), nWorkers, runningFlag);
        Verify.endAtomic();
        masterAgent.start();
        runningFlag.set();
        try {
            masterAgent.join();
        } catch (InterruptedException e) {
            System.out.println("Master agent interrupted");
        }
    }
}
