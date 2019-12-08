package com.domenic.BrickLayerAuthoritativeServer.models;

public class Message {
	private Action action;
	private Object obj;
	
	public Action getAction() {
		return action;
	}
	public void setAction(Action action) {
		this.action = action;
	}
	public Object getObj() {
		return obj;
	}
	public void setObj(Object obj) {
		this.obj = obj;
	}
	@Override
	public String toString() {
		return "Message [action=" + action + ", obj=" + obj + "]";
	}

	
}
