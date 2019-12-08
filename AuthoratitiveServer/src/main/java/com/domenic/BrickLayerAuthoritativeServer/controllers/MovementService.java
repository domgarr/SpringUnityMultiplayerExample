package com.domenic.BrickLayerAuthoritativeServer.controllers;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.springframework.stereotype.Component;


import com.domenic.BrickLayerAuthoritativeServer.io.ClientHandler;
import com.domenic.BrickLayerAuthoritativeServer.models.Position;

@Component
public class MovementService {
	//TODO: Store the variables in the DB.
	private double x, y;
	private double speed = 5;
	 
	
	public MovementService() {
		this.x = 0;
		this.y = 0;
	}
	
	/*
	 * Calculate player's new position.
	 */
	public Position command(String sessionId, double deltaTime, double horizontal , double vertical) {
		Position playerCurrentPosition = ClientHandler.players.get(sessionId).getPosition();
		Vector2D tempCurrentPosition = new Vector2D(playerCurrentPosition.getX(), playerCurrentPosition.getY());
		
		Vector2D newPositionVector = new Vector2D(horizontal, vertical);
		newPositionVector = newPositionVector.normalize().scalarMultiply(speed).scalarMultiply(deltaTime);
		newPositionVector = newPositionVector.add(tempCurrentPosition);
		//Update players new position server-side.
		playerCurrentPosition.setX(newPositionVector.getX());
		playerCurrentPosition.setY(newPositionVector.getY());
		
		return playerCurrentPosition;
	}
	
}
