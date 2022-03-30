package pcd.ass01.parallel;

import pcd.ass01.parallel.model.SimulationDataFactory;
import pcd.ass01.parallel.monitor.Flag;
import pcd.ass01.ui.SimulationView;

/**
 * Bodies simulation - legacy code: sequential, unstructured
 *
 * @author aricci
 */
public class GUIBodySimulationMain {

    public static void main(String[] args) {
        int nWorkers = Runtime.getRuntime().availableProcessors();
        Flag runningFlag = new Flag();
        SimulationView viewer = new SimulationView(620, 620);
        SimulationDataFactory dataFactory = new SimulationDataFactory();
        MasterAgent masterAgent = new MasterAgent(viewer,dataFactory.testBodySet4_many_bodies(5000, 1000), nWorkers, runningFlag);
        masterAgent.start();
        Controller controller = new Controller(runningFlag);
        viewer.addListener(controller);
    }
}
