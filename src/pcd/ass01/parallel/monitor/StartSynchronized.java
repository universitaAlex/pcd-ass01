package pcd.ass01.parallel.monitor;

public class StartSynchronized {

	private boolean started;

	public StartSynchronized(){
		started = false;
	}
	
	public synchronized void waitStart() {
		while (!started) {
			try {
				wait();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}
	public synchronized void stop() {
		started = false;
	}

	public synchronized void notifyStarted() {
		started = true;
		notifyAll();
	}
}
