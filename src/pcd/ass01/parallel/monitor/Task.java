package pcd.ass01.parallel.monitor;

import pcd.ass01.model.Body;

public class Task {

	private final Body body;

	public Task(Body body) {
		this.body = body;
	}

	public Body getBody() {
		return body;
	}
}