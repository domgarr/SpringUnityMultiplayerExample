using System;
using System.Collections;
using System.Collections.Generic;
using System.Net.WebSockets;
using System.Text;
using System.Threading;
using UnityEngine;

public class Controller : MonoBehaviour
{
    // Start is called before the first frame update
    void Start()
    {
        
    }

    // Update is called once per frame
    void Update()
    {
        float h = Input.GetAxisRaw("Horizontal");
        float v = 0;

        if (h != 0 || v != 0)
        {
            SendAxis(Time.deltaTime, h, v);
        }
    }

    async void SendAxis(float deltaTime, float h, float v)
    {
        MovementData movementData = new MovementData();
        movementData.action = GameConstants.UPDATE_POSITION;
        movementData.deltaTime = deltaTime;
        movementData.h = h;
        movementData.v = v;

        string json = JsonUtility.ToJson(movementData);
        Debug.Log(json);
        ArraySegment<byte> b = new ArraySegment<byte>(Encoding.UTF8.GetBytes(json));
        await GameClientWebSocket.getInstance().GetClientWebSocket().SendAsync(b, WebSocketMessageType.Text, true, CancellationToken.None);
    }

    [Serializable]
    public class MovementData : MessageType
    {
        public float v;
        public float h;
        public float deltaTime;
    }

    [Serializable]
    public class MessageType
    {
        public string action;

    }
}
