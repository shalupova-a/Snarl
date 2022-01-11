package Game.NetworkMessage;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MessageType {
    @JsonProperty("welcome") WELCOME("welcome"),
    @JsonProperty("start-level") START_LEVEL("start-level"),
    @JsonProperty("move") MOVE("move"),
    @JsonProperty("player-update") PLAYER_UPDATE("player-update"),
    @JsonProperty("end-level") END_LEVEL("end-level"),
    @JsonProperty("name") NAME("name"),
    @JsonProperty("result") RESULT(null),
    @JsonProperty("end-game") END_GAME("end-game");

    private String messageTypeString;

    MessageType(String messageTypeString) {
        this.messageTypeString = messageTypeString;
    }

    public String getMessageTypeString() {
        return messageTypeString;
    }
}
