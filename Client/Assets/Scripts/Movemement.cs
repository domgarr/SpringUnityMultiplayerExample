

using UnityEngine;
using UnityEngine.EventSystems;

public class Movemement : MonoBehaviour, Movemement.MovemementEvents
{
    public Rigidbody2D rb;

    // Start is called before the first frame update
    void Start()
    {

    }

    // Update is called once per frame
    void Update()
    {

    }

    void FixedUpdate()
    {

    }


    public void updatePosition(GameManagement.UpdatePosition updatePosition)
    {
        rb.MovePosition(new Vector2(updatePosition.position.x, updatePosition.position.y));
    }

    public interface MovemementEvents : IEventSystemHandler
    {
        void updatePosition(GameManagement.UpdatePosition position);
    }
}