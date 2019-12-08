using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public static class GameConstants
{
    // ws means the Uri can be either http or https 
    public const  string SERVER_URI = "ws://localhost:8080/server";
    public const int BUFFER_SIZE = 1024;

    //Events sent to and recieved from WebSockets. 
    public const string REMOVE_PLAYER = "REMOVE_PLAYER";
    public const string UPDATE_POSITION = "UPDATE_POSITION";
    public const string PLAYER_JOINED = "PLAYER_JOINED";
    public const string NEW_PLAYER_JOINED = "NEW_PLAYER_JOINED";
    public const string FETCH_EXISTING_PLAYERS = "FETCH_EXISTING_PLAYERS";
}
