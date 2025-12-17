package socket_server.domain.game.state;

import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;
import socket_server.domain.game.enumType.GameEventType;

@Getter
@Setter
public class GameManager {
    private GameState currentState;
    private final GameContext context;

    public GameManager(String roomId, Object... services) {
        // This is not a good way to pass services. I will refactor this later.
        // For now, I will assume the services are passed in the correct order.
        this.context = new GameContext(roomId, this, services);
        this.currentState = new InitialState(); // Need to create this state
    }

    public Mono<Void> handleEvent(GameEventType eventType, String... args) {
        return currentState.handleEvent(context, eventType, args);
    }
}
