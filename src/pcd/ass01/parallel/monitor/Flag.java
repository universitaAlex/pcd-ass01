package pcd.ass01.parallel.monitor;

public class Flag {

	private boolean flag;
	
	public Flag() {
		flag = false;
	}
	
	public synchronized void reset() {
		flag = false;
	}
	
	public synchronized void set() {
		flag = true;
		notifyAll();
	}

	public synchronized void waitSet() {
		while (!flag) {
			try {
				wait();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public synchronized boolean isSet() {
		return flag;
	}
}
