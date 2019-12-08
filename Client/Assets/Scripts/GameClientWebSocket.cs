using System.Collections;
using System.Collections.Generic;
using System.Net.WebSockets;
using System.Threading;
using System;
using System.Text;
using UnityEngine;
using UnityEngine.EventSystems;
using System.Threading.Tasks;

public class GameClientWebSocket
{
    private static GameClientWebSocket instance;
    private Uri uri = new Uri(GameConstants.SERVER_URI);
    private ClientWebSocket cws;
    private ArraySegment<byte> buffer = new ArraySegment<byte>(new byte[1024]);

    private GameClientWebSocket()
    {
        
    }

    public static GameClientWebSocket getInstance()
    {
        if (instance == null)
        {
            instance = new GameClientWebSocket();
        }

        return instance;
    }

    public async Task<bool> Connect()
    {
        cws = new ClientWebSocket();
        try
        {
            //Non-blocking way to start a task. This will not interrupt Unity from executing lifecycle methods
            await cws.ConnectAsync(uri, CancellationToken.None);
            if (isConnected())
            {
                return true;
            }
        }
        catch (Exception e)
        {
            Debug.Log("Error Connecting : " + e);
        }

        return false;
    }

    public bool isConnected()
    {
        return cws.State == WebSocketState.Open;
    }

    public ClientWebSocket GetClientWebSocket()
    {
        return this.cws;
    }

    public ArraySegment<byte> getBuffer()
    {
        return new ArraySegment<byte>(new byte[GameConstants.BUFFER_SIZE]);
    }
           




}
