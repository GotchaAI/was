package socket_server.domain.game.enumType;

public enum GameMode {
    TRICK_MYOMYO("묘묘를 속여라", 2, 8),
    LULU_ART_EXAM("루루의 미대입시", 1, 1);

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
