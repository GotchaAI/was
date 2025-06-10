package gotcha_domain.gamehistory;

import gotcha_domain.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserGameHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_history_id")
    private GameHistory gameHistory;


    @Builder
    public UserGameHistory(User player, GameHistory gameHistory) {
        this.player = player;
        this.gameHistory = gameHistory;
    }

}
