package Game.NetworkMessage;

public class PlayerMove {
    //    { "type": "move",
//            "to": (maybe-point)
//    }
    private MessageType type;
    private Integer[] to;

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Integer[] getTo() {
        return to;
    }

    public void setTo(Integer[] to) {
        this.to = to;
    }
}
