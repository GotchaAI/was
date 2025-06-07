package gotcha_domain.gamehistory;

import gotcha_common.entity.BaseTimeEntity;
import gotcha_domain.report.BugReport;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameHistory extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private GameType gameType;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @NotNull
    private int totalRounds;

    @NotNull
    private Boolean playerWon; // AI or Player

    @NotNull
    private Integer aiScore;

    @NotNull
    private Integer playerScore;

    @OneToMany(mappedBy = "gameHistory")
    private List<BugReport> bugReports = new ArrayList<>();

    @OneToMany(mappedBy = "gameHistory")
    private List<UserGameHistory> userGameHistories = new ArrayList<>();

    @OneToMany(mappedBy = "gameHistory")
    private List<RoundHistory> roundHistories = new ArrayList<>();

    @Builder
    public GameHistory(
            GameType gameType,
            Difficulty difficulty,
            int totalRounds,
            Boolean playerWon,
            Integer aiScore,
            Integer playerScore
    ){
        this.gameType = gameType;
        this.difficulty = difficulty;
        this.totalRounds = totalRounds;
        this.playerWon = playerWon;
        this.aiScore = aiScore;
        this.playerScore = playerScore;
    }


}


