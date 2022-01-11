package Game.NetworkMessage;

public enum NetworkOutMessage {
    STEPPED_ON_KEY("Key", "player {0} found key"),
    STEPPED_ON_EXIT("Exit", "player {0} exited"),
    STEPPED_ON_EXIT_NO_KEY("ExitNoKey", "player {0} found exit but no key!"),
    EJECTED("Ejected", "player {0} died"),
    INVALID("Invalid", ""),
    OK_STAYED_PUT("OK", "player {0} stayed put"),
    OK("OK", "player {0} moved"),
    DYNAMIC_DAMAGE_MESSAGE("", ""),
    DAMAGE_MESSAGE("", "{0}, {1} in combat! HP: {1}-{2}, {0}-{3}"),
    DAMAGE_KILLED_ENEMY("", "player {0} killed a {1}!");

    private String networkValue;
    private String networkMessage;

    NetworkOutMessage(String networkValue, String networkMessage) {
        this.networkValue = networkValue;
        this.networkMessage = networkMessage;
    }

    public String getNetworkValue() {
        return networkValue;
    }

    public String getNetworkMessage() {
        return networkMessage;
    }

    public void setNetworkMessage(String networkMessage) {
        // we don't change enum constants unless its for dynamic message.
        if (this.equals(NetworkOutMessage.DYNAMIC_DAMAGE_MESSAGE)) {
            this.networkMessage = networkMessage;
        }
    }
}
