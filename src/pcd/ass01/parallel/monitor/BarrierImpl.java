package pcd.ass01.parallel.monitor;

/*
 * Barrier - to be implemented
 */
public class BarrierImpl implements Barrier {

	private int nParticipants;
	private int nHits;
	
	public BarrierImpl(int nParticipants) {
		this.nParticipants = nParticipants;
		this.nHits=0;
	}
	
	@Override
	public synchronized void hitAndWaitAll() throws InterruptedException {
		nHits++;
		while (nHits<nParticipants){
			wait();
		}
		notify();
	}

	
}
