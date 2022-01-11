package Game;

public class CmdLineOption {

    private String levelsFileName;
    private int players;
    private int levelToStart;
    private boolean isObserverMode;
    private String address;
    private String type;
    private int port;
    private int wait;
    private int clients;
    private int games;

    public CmdLineOption(String levelsFileName, int levelToStart, boolean isObserverMode,
                         String address, int port, String type, int wait, int clients, int games) {
        this.levelsFileName = levelsFileName;
        this.players = players;
        this.levelToStart = levelToStart;
        this.isObserverMode = isObserverMode;
        this.address = address;
        this.type = type;
        this.port = port;
        this.wait = wait;
        this.clients = clients;
        this.games = games;
    }

    public String getLevelsFileName() {
        return this.levelsFileName;
    }

    public int getPlayers() {
        return this.players;
    }

    public int getLevelToStart() {
        return this.levelToStart;
    }

    public boolean getIsObserverMode() {
        return this.isObserverMode;
    }

    public String getAddress() { return this.address; }

    public int getPort() { return this.port; }

    public String getType() { return this.type; }

    public int getWait() {
        return this.wait;
    }

    public int getClients() {
        return this.clients;
    }

    public int getGames() {
        return this.games;
    }
}
