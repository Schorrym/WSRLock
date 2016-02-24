package de.mariokramer.wsrlock.model;

public class Message<T> {

	private String task;
	private T object;
	
	public Message() {}
	public Message(T object, String task) {
		this.object = object;
		this.task = task;
	}
	
	public String getTask() {
		return task;
	}
	public void setTask(String task) {
		this.task = task;
	}
	public T getObject() {
		return object;
	}
	public void setObject(T object) {
		this.object = object;
	}
}
