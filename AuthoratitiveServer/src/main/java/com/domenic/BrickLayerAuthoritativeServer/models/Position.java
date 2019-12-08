package com.domenic.BrickLayerAuthoritativeServer.models;

/* 
 * This object contains the user's new position with Time.DeltaTime taken into consideration.
 */
public class Position{
	private int id;
	private double x;
	private double y;
	
	

	public Position( double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	
}
