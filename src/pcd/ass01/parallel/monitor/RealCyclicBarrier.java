package pcd.ass01.parallel.monitor;

/*
 * Barrier - to be implemented
 */
public class RealCyclicBarrier implements CyclicBarrier {

	private int nParticipants;
	private int nHits;
	private int nOut;
	private boolean broken;

	public RealCyclicBarrier(int nParticipants) {
		this.nParticipants = nParticipants;
		this.nHits=0;
		this.nOut=0;
	}
	
	@Override
	public synchronized void hitAndWaitAll() {
		if (this.broken) {
			throw new IllegalStateException("Barrier is broken.");
		}
		nHits++;
		while (nHits<nParticipants){
			try {
				wait();
			} catch (InterruptedException e) {}
		}
		nOut++;
		if (nOut == 1) {
			this.broken = true;
		} else if (nOut == nHits){
			this.broken = false;
			this.nOut = 0;
			this.nHits = 0;
		}
		notify();
	}
	public synchronized void reset() {
	}
}
