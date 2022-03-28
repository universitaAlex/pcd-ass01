package pcd.ass01.parallel.monitor;

public class IterationTracker {

    private long iteration;
    private boolean terminated;

    public IterationTracker() {
        this.iteration = -1;
    }

    /**
     * @param iteration the iteration to be executed
     * @return true if it should execute this iteration or false if it isn't allowed
     */
    public synchronized boolean waitIteration(long iteration) {
        while (this.iteration != iteration && !terminated) {
            try {
                wait();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        return !terminated;
    }

    public synchronized void setCurrentIteration(long iteration) {
        this.iteration = iteration;
        notifyAll();
    }
    public synchronized void terminate() {
        this.terminated = true;
        notifyAll();
    }
}
