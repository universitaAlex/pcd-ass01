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
        //SimulationView viewer = new SimulationView(620,620);
        long start = System.nanoTime();

        Simulator sim = new Simulator(new ConsoleSimulationDisplay());
        sim.execute(50000);

        long end = System.nanoTime();
        long elapsedTime = end - start;

        long elapsedTimeSeconds = TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);

        System.out.println("Elapsed time: " + (elapsedTimeSeconds));
    }
}
