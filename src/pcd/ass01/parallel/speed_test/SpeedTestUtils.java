package pcd.ass01.parallel.speed_test;

import java.util.concurrent.TimeUnit;

public class SpeedTestUtils {

    private SpeedTestUtils() {}

    static public void test(int nExecution, Runnable runnable) {
        double elapsedTimeSum = 0;
        for (int i = 0; i < nExecution; i++) {
            long start = System.nanoTime();

            runnable.run();
            long end = System.nanoTime();
            long elapsedTime = end - start;

            double elapsedTimeSeconds = TimeUnit.MILLISECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS) / 1000.0;
            elapsedTimeSum += elapsedTimeSeconds;
        }
        double elapsedTimeAvg = elapsedTimeSum / nExecution;
        System.out.println("Elapsed time: " + (elapsedTimeAvg) + " s");
    }
}
