package de.mariokramer.wsrlock.model;

/**
 * Message Model to send and receive messages via WebSockets.
 * Used by Springs AnnotationMethodMessageHandler to convert from/to JSON Objects
 * The private variables name should be same here as sended from the client
 * @author Mario Kramer
 *
 */
public class Message {

	private String task;
	private Long docId;
	private String notify;
	
	public String getNotify() {
		return notify;
	}
	public void setNotify(String notify) {
		this.notify = notify;
	}
	public String getTask() {
		return task;
	}
	public void setTask(String task) {
		this.task = task;
	}
	public Long getDocId() {
		return Long.valueOf(docId);
	}
	public void setDocId(String docId) {
		this.docId =  Long.valueOf(docId);
	}


}
