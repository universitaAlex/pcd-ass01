package pcd.ass01.parallel.monitor.latch;

public class RealLatch implements Latch {

    private final int count;
    private int currentCount;
    public RealLatch(int count) {
        this.count = count;
        this.currentCount = 0;
    }

    @Override
    public synchronized void await() throws InterruptedException {
        while (currentCount < count) {
            wait();
        }
    }

    @Override
    public synchronized void resetCount() {
        this.currentCount = 0;
    }

    @Override
    public synchronized void countDown() {
        currentCount++;
        if (currentCount == count) {
            notifyAll();
        }
    }


}
