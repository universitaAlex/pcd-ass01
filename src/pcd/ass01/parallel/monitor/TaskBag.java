package pcd.ass01.parallel.monitor;

import pcd.ass01.model.Body;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TaskBag {

	private final LinkedList<Task> buffer;
	private final List<Body> results = new ArrayList<>();

	public TaskBag() {
		buffer = new LinkedList<>();
	}

	public synchronized void clear() {
		buffer.clear();
		results.clear();
	}
	
	public synchronized void addNewTask(Task task) {
		buffer.addLast(task);
		notifyAll();
	}

	public synchronized Task getATask() {
		while (buffer.isEmpty()) {
			try {
				wait();
			} catch (Exception ex) {}
		}
		return buffer.removeFirst(); 
	}

	public synchronized void addNewResult(Body task) {
		results.add(task);
	}

	public synchronized List<Body> getResults() {
		return new ArrayList<>(results);
	}
	
}
