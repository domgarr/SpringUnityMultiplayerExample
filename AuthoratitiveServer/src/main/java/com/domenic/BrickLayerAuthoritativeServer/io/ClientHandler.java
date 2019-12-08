package com.domenic.BrickLayerAuthoritativeServer.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.domenic.BrickLayerAuthoritativeServer.controllers.MovementService;
import com.domenic.BrickLayerAuthoritativeServer.models.Account;
import com.domenic.BrickLayerAuthoritativeServer.models.Action;
import com.domenic.BrickLayerAuthoritativeServer.models.Message;
import com.domenic.BrickLayerAuthoritativeServer.models.MovementData;
import com.domenic.BrickLayerAuthoritativeServer.models.Position;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

//TODO: Rename handler
public class ClientHandler extends TextWebSocketHandler  {
	
	public static HashMap<String, Account> players = new HashMap<>();
	public static HashMap<String, WebSocketSession> sessions = new HashMap<>();

	@Autowired
	MovementService control;
	
	// Get a new ObjectMapper configured to fail on unknown properties found in the JSON
	private ObjectMapper getNewAndConfiguredOm(){
		ObjectMapper oM = new ObjectMapper();
		oM.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return oM;
	}
	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
		ObjectMapper oM = getNewAndConfiguredOm();
	
		try {
			Message messageRecieved = oM.readValue(message.getPayload(), Message.class);
			handleRecievedMessages(messageRecieved, session, message);
		} catch (Exception e1) {
			e1.printStackTrace();
		}	
	}
	
	private void handleRecievedMessages(Message messageReceived, WebSocketSession session, TextMessage textMessage) throws IOException {
		switch(messageReceived.getAction()){
			case UPDATE_POSITION:
				updatePosition(session, textMessage);
				broadcastPositions(session.getId());
				break;
			case FETCH_EXISTING_PLAYERS:
				fetchExistingPlayers(session);
				break;
				
		default:
			break;
		}
	}

	private void fetchExistingPlayers(WebSocketSession session ) {
		Gson gson;
		HashMap<String, Object> hm;
		String json;
		
		int id = players.get(session.getId()).getId();
		
		//Iterate through all players skipping accounts with the identical ID
		Iterator<Entry<String, Account>> accountIt = players.entrySet().iterator();
		
		while(accountIt.hasNext()) {
			Entry<String,Account> pair = accountIt.next();
			Account account = pair.getValue();
			if(id == account.getId()) {
				continue;
			}
			System.out.println("id that called fetch: "+ id + "- Id: " + account.getId() );

			
			gson = new Gson();
			hm = new HashMap<>();
			
			//Add Action
			hm.put("action", Action.NEW_PLAYER_JOINED);
			//Distinguish GameObjects using an id.
			hm.put("id", account.getId());
			//Add players starting position
			//TODO: Fetch from db.
			hm.put("position", account.getPosition());
			json = gson.toJson(hm);
			
			System.out.println("Fetching existing player: " + json);

			
			try {
				session.sendMessage(new TextMessage(json));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		//Create a Account object to hold the session Id, to be used for broadcasting. 
		Account account = new Account(session.getId());
		//Set a unique ID. This unique will be generated from the DB.
		account.setId();
		
		//TODO: Get position from DB.
		Position position = new Position(0,0);
		//Position needs an ID so that when we broadcast we should which GameObject to mutate;
		position.setId(account.getId());
		account.setPosition(position);
		
		//Place player and session into a HashMap, using the Session Id as they key.
		players.put(account.getSessionId(), account);
		sessions.put(session.getId(), session);
		
		playerJoined(session, account.getId());
	}
	
	public void playerJoined(WebSocketSession session, int id) {
		//Send JSON to Client. The player the owner of the Client will be playing.
		Gson gson = new Gson();
		HashMap<String, Object> hm = new HashMap<>();
		
		//Add Action
		hm.put("action", Action.PLAYER_JOINED);
		//Distinguish GameObjects using an id.
		hm.put("id", id);
		//Add players starting position
		//TODO: Fetch from db.
		hm.put("position", new Position(0,0));
		
		String json = gson.toJson(hm);
		System.out.println(json);

		try {
			session.sendMessage(new TextMessage(json));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(players.size());
		broadcastNewPlayerJoined(session);
	}
	
	public void broadcastNewPlayerJoined(WebSocketSession session ) {
		Gson gson;
		HashMap<String, Object> hm;
		String json;
		
		//Send Messages telling all existing player that this Player has joined the game.
				//BroadCast to all players that a NEW_PLAYER_JOINED
				if(sessions.size() > 0 ) {
					Iterator<Entry<String, WebSocketSession>> sessionsIt = sessions.entrySet().iterator();
					
					while(sessionsIt.hasNext()) {
						Entry<String, WebSocketSession> pair = sessionsIt.next();
						WebSocketSession currSession = pair.getValue();
						//We don't want to send this Message to playres of the same ID. 
						if(session.getId().compareTo(currSession.getId()) == 0) {
							continue;
						}
						
						gson = new Gson();
						hm = new HashMap<>();
					
						//Add Action
						hm.put("action", Action.NEW_PLAYER_JOINED);
						//Distinguish GameObjects using an id.
						hm.put("id", players.get(session.getId()).getId());
						//Add players starting position
						//TODO: Fetch from db.
						hm.put("position", players.get(session.getId()).getPosition());
						json = gson.toJson(hm);
						
						System.out.println(json);
						WebSocketSession sessionToSendTo = sessions.get(pair.getKey());
						try {
							sessionToSendTo.sendMessage(new TextMessage(json));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		Gson gson = new Gson();
		HashMap<String, Object> hm = new HashMap<>();
		hm.put("action", Action.REMOVE_PLAYER.toString());
		hm.put("id", players.get(session.getId()).getId());
		String json = gson.toJson(hm);
		
		players.remove(session.getId());
		sessions.remove(session.getId());
		
		Iterator<Entry<String, WebSocketSession>> sessionIt = sessions.entrySet().iterator();
		while(sessionIt.hasNext()) {
			Entry<String, WebSocketSession> pair = sessionIt.next();
			pair.getValue().sendMessage(new TextMessage(json));
		}
	}
	
	public void broadcastPositions(String sessionId) {
		Account account = players.get(sessionId);
		
		HashMap<String, Object> hm = new HashMap<>();
		hm.put("action", Action.UPDATE_POSITION.toString());
		hm.put("position", account.getPosition());
		
		Gson gson = new Gson();
		String json = gson.toJson(hm);
		
		Iterator<Entry<String, WebSocketSession>> sessionIt = sessions.entrySet().iterator();
	
		//Send an array of positions to all clients.
		while(sessionIt.hasNext()) {
			
			Entry<String,WebSocketSession> pair = sessionIt.next();
			if(sessionId.compareTo(pair.getKey()) == 0) {
				continue;
			}
			try {
				pair.getValue().sendMessage(new TextMessage(json));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
			
	}
	
	public void updatePosition(WebSocketSession session, TextMessage message) throws IOException  {
		ObjectMapper oM = getNewAndConfiguredOm();
		
		//Store the JSON message from client into a java object.
		MovementData movementData = oM.readValue(message.getPayload(), MovementData.class);
		/* Using the information given from client, pass the info into command method (need to rename) which will calculate the players new position and
		serialize into a JSON. */
		Position playerNewPosition = control.command(session.getId(), movementData.getDeltaTime(), movementData.getH(), movementData.getV());
		
		HashMap<String,Object> hm = new HashMap<>();
		hm.put("position", playerNewPosition);
		hm.put("action", Action.UPDATE_POSITION.toString());
	 
		
		Gson gson = new Gson();
		String jsonToSend = gson.toJson(hm);
		
		System.out.println(jsonToSend);
		
		//Send the client the user's new position
		session.sendMessage( new TextMessage(jsonToSend) );
		Account account = players.get(session.getId());
		account.setPosition(playerNewPosition);
		
		//Send positions to all connected clients and skip this session position since it's already sent.
	}

}
