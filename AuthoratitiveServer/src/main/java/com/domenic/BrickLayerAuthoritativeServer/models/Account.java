package com.domenic.BrickLayerAuthoritativeServer.models;

public class Account {
	public static int ID_COUNTER = 1;
	private int id;
	private String sessionId;
	private Position position;
	
	public int getId() {
		return id;
	}

	public void setId() {
		this.id = ID_COUNTER;
		ID_COUNTER++;
	}

	public Account(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public String getSessionId() {
		return sessionId;
	}
	
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public void setPosition(Position position) {
		this.position = position;
	}
}
