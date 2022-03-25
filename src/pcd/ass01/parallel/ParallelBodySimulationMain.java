package pcd.ass01.parallel;

import pcd.ass01.model.ConsoleSimulationDisplay;
import pcd.ass01.ui.SimulationView;

import java.util.concurrent.TimeUnit;

/**
 * Bodies simulation - legacy code: sequential, unstructured
 * 
 * @author aricci
 */
public class ParallelBodySimulationMain {

    public static void main(String[] args) {
        SimulationView viewer = new SimulationView(620, 620);
        SimulationDataFactory dataFactory = new SimulationDataFactory();
        Simulator sim = new Simulator(viewer,dataFactory.testBodySet4_many_bodies(5000));
        Controller controller = new Controller(sim);
        viewer.addListener(controller);
        sim.configure();
    }

    private static void testSpeedMain() {
        int nExecution = 10;
        //SimulationView viewer = new SimulationView(620,620);
        double elapsedTimeSum = 0;
        for (int i = 0; i < nExecution; i++) {
            long start = System.nanoTime();

            //Simulator sim = new Simulator(new ConsoleSimulationDisplay());
            //sim.configure(2000);

            long end = System.nanoTime();
            long elapsedTime = end - start;

            double elapsedTimeSeconds = TimeUnit.MILLISECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS) / 1000.0;
            elapsedTimeSum+=elapsedTimeSeconds;
        }
        double elapsedTimeAvg = elapsedTimeSum/nExecution;
        System.out.println("Elapsed time: " + (elapsedTimeAvg) + " s");
    }
}
