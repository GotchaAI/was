package socket_server.domain.game.enumType;

public enum GameMode {
    GAME_1("게임1", 2, 2),
    GAME_2("게임2", 4, 8);

    private final int minPlayers;
    private final int maxPlayers;

    GameMode(String description, int minPlayers, int maxPlayers) {
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }
}
