package pcd.ass01.parallel.monitor;

public interface Barrier {

	void hitAndWaitAll() throws InterruptedException;

}
