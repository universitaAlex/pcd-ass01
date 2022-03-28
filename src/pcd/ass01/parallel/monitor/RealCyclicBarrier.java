package pcd.ass01.parallel.monitor;

import java.util.concurrent.BrokenBarrierException;

public class RealCyclicBarrier implements CyclicBarrier {

	private final int nParticipants;
	private int nHits;
	private int nOut;
	private boolean broken;

	public RealCyclicBarrier(int nParticipants) {
		this.nParticipants = nParticipants;
		this.nHits=0;
		this.nOut=0;
		this.broken = false;
	}
	
	@Override
	public synchronized void hitAndWaitAll() throws BrokenBarrierException {
		if (this.broken) {
			throw new BrokenBarrierException();
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
}
