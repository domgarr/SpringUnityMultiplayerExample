package com.domenic.BrickLayerAuthoritativeServer.models;

/*
 * Populated client-side. 
 */
public class MovementData {
	double deltaTime;
	double v;
	double h;
	
	
	public MovementData() {
	}
	
	public double getDeltaTime() {
		return deltaTime;
	}

	public void setDeltaTime(double deltaTime) {
		this.deltaTime = deltaTime;
	}

	public double getV() {
		return v;
	}

	public void setV(double v) {
		this.v = v;
	}

	public double getH() {
		return h;
	}

	public void setH(double h) {
		this.h = h;
	}
	
	
}
