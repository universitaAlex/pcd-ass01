package pcd.ass01.parallel.monitor;

public interface Latch {

    void countDown();

    void await() throws InterruptedException;

    void resetCount();
}
