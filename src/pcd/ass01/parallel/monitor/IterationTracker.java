package pcd.ass01.parallel.monitor;

import pcd.ass01.model.Body;

import java.util.ArrayList;
import java.util.List;

public class IterationTracker {

	private long iteration;
	private final List<Body> results = new ArrayList<>();

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
	public synchronized void clearResults() {
		results.clear();
	}

	public synchronized void addResults(List<Body> body) {
		results.addAll(body);
	}
	public synchronized List<Body> getResults() {
		return new ArrayList<>(results);
	}
}
