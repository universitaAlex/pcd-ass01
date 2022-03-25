package pcd.ass01.parallel.monitor;

import pcd.ass01.model.Body;

public class Task {

	private final TaskType taskType;
	private final Body body;

	public Task(TaskType taskType, Body body) {
		this.taskType = taskType;
		this.body = body;
	}

	public TaskType getTaskType() {
		return taskType;
	}

	public Body getBody() {
		return body;
	}


	public enum TaskType {
		COMPUTE_VELOCITY, COMPUTE_POSITION;
	}
}