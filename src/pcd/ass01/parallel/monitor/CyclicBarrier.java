package pcd.ass01.parallel.monitor;

import java.util.concurrent.BrokenBarrierException;

public interface CyclicBarrier {

	void hitAndWaitAll() throws BrokenBarrierException;
	void reset();
}
