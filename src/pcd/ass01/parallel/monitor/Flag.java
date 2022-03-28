package pcd.ass01.parallel.monitor;

public class Flag {

    private boolean flag;

    public Flag() {
        flag = false;
    }

    public synchronized void reset() {
        flag = false;
    }
    public synchronized void awaitSet() {
        while (!flag) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
    }

    public synchronized void set() {
        flag = true;
        notifyAll();
    }

    public synchronized boolean isSet() {
        return flag;
    }
}
