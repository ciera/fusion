package edu.cmu.cs.fusion;

public class TimeoutException extends RuntimeException {

	private long time;

	public TimeoutException(long time) {
		this.time = time;
	}

	public long getTime() {
		return time;
	}

}
