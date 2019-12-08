
using System.Collections.Generic;
using System.Net.WebSockets;
using System.Threading;
using System;
using System.Text;
using UnityEngine;
using UnityEngine.EventSystems;

public partial class GameManagement : MonoBehaviour
{

    GameClientWebSocket gcws;
    public GameObject playerControlledPrefab;
    public GameObject playerPrefab;
    private readonly string PLAYER_PREFAB_OBJECT_NAME = "PLAYER";
    Dictionary<int, GameObject> players;

    void Awake()
    {

        players = new Dictionary<int, GameObject>();

        this.gcws = GameClientWebSocket.getInstance(); //Init WebSocket in Awake()!
        onConnect();
        
    }

    void iteretePlayer()
    {
        foreach(KeyValuePair<int, GameObject> entry in players)
        {

        }
    }

    void updatePlayerPosition(UpdatePosition updatePosition)
    {
          GameObject playerPrefabToUpdate = players[updatePosition.position.id];
          ExecuteEvents.Execute<Movemement.MovemementEvents>(playerPrefabToUpdate, null, (x, y) => { x.updatePosition(updatePosition); });
    }

    void removePlayer(int id)
    {
        Destroy(players[id].gameObject);
            players.Remove(id);
    }

    private void Start()
    {
        //this.playerControlledPrefab = Instantiate(playerControlledPrefab, new Vector3(0, 0, 0), Quaternion.identity);
    }

    // Update is called once per frame
    void Update()
    {
        
    }

    async void getMessagesFromServer()
    {
        ArraySegment<byte> buffer = GameClientWebSocket.getInstance().getBuffer();

        if(buffer == null)
        {
            Debug.Log("ERROR: The buffer is empty!");
        }
        WebSocketReceiveResult r = await this.gcws.GetClientWebSocket().ReceiveAsync(buffer, CancellationToken.None);

        performAction(r, buffer);


        getMessagesFromServer();
    }

    public async void onConnect()
    {
        bool status = await gcws.Connect();
        if (status)
        {
            getMessagesFromServer();
            
        }
       
    }

    private void performAction(WebSocketReceiveResult r, ArraySegment<byte> buffer)
    {

       
        String bytesToString = Encoding.UTF8.GetString(buffer.Array, 0, r.Count);
        MessageType message = JsonUtility.FromJson<MessageType>(bytesToString);
        Debug.Log(bytesToString);

        switch (message.action)
        {
            case GameConstants.UPDATE_POSITION:
                UpdatePosition position = JsonUtility.FromJson<UpdatePosition>(bytesToString);
                //"x" is the handler, y is the addiontal data you want to pass.
                updatePlayerPosition(position);
                Debug.Log(message.action);
                break;
            //Add the Player who is playing on his Client to the game.
            case GameConstants.PLAYER_JOINED:
                PlayerJoined playerJoined = JsonUtility.FromJson<PlayerJoined>(bytesToString);
                Debug.Log("Player joined!");
                this.playerControlledPrefab.name = PLAYER_PREFAB_OBJECT_NAME + playerJoined.id;
                this.playerControlledPrefab = Instantiate(playerControlledPrefab, new Vector3(playerJoined.position.x, playerJoined.position.y, 0), Quaternion.identity);
                players.Add(playerJoined.id, this.playerControlledPrefab);
                fetchExistingPlayers();
                break;
            //Add New Players that are not the local player.
            case GameConstants.NEW_PLAYER_JOINED:
                PlayerJoined newPlayerJoined = JsonUtility.FromJson<PlayerJoined>(bytesToString);
                Debug.Log("New Player joined!");
                this.playerPrefab.name = PLAYER_PREFAB_OBJECT_NAME + newPlayerJoined.id;
                players.Add(newPlayerJoined.id,Instantiate(playerPrefab, new Vector3(newPlayerJoined.position.x, newPlayerJoined.position.y, 0), Quaternion.identity));
                break;
            case GameConstants.REMOVE_PLAYER:
                Debug.Log("Player Removed");
                removePlayer(message.id);
                break;
            default:
                break;
        }
    }

    async void fetchExistingPlayers()
    {
        Debug.Log("Fetching existing players");
        MessageType message = new MessageType();
        message.action = GameConstants.FETCH_EXISTING_PLAYERS;
        string json = JsonUtility.ToJson(message);
       
        ArraySegment<byte> b = new ArraySegment<byte>(Encoding.UTF8.GetBytes(json));
        await GameClientWebSocket.getInstance().GetClientWebSocket().SendAsync(b, WebSocketMessageType.Text, true, CancellationToken.None);

    }


    [Serializable]
    public class MessageType
    {
        public int id;
        public string action;
    }

    [Serializable]
   public class UpdatePosition : MessageType
    {
        
        public Position position;
    }

    [Serializable]
    class UpdateAllPosition : MessageType
    {
        public Position[] position;
    }

    [Serializable]
    public class Position
    {
        public int id;
        public float x;
        public float y;

        public string toString()
        {
            return x + ":" + y;
        }
    }

    [Serializable]
    public class PlayerJoined : MessageType
    {
        public int id;
        public Position position;
    }
}
