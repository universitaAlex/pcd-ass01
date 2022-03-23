package pcd.ass01.parallel.monitor;

public class IterationTracker {

	private long iteration;

	public IterationTracker(){
		this.iteration = -1;
	}
	
	public synchronized void waitIteration(long iteration) {
		while (this.iteration != iteration) {
			try {
				wait();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}
	public synchronized void setCurrentIteration(long iteration) {
		this.iteration = iteration;
		notifyAll();
	}
}
