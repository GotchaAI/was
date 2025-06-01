package gotcha_domain.gamehistory;

import gotcha_common.entity.BaseTimeEntity;
import gotcha_domain.report.BugReport;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private Integer totalRounds;

    @NotNull
    private String winner;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "final_scores", columnDefinition = "JSON")
    private Map<String, Integer> finalScores;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "game_players", columnDefinition = "JSON")
    private List<GamePlayerInfo> gamePlayers;


    @OneToMany(mappedBy = "gameHistory")
    private List<BugReport> bugReports = new ArrayList<>();

    @OneToMany(mappedBy = "gameHistory")
    private List<UserGameHistory> userGameHistories = new ArrayList<>();

    @OneToMany(mappedBy = "gameHistory")
    private List<RoundHistory> roundHistories = new ArrayList<>();

    @Builder
    public GameHistory(GameType gameType, Difficulty difficulty){
        this.gameType = gameType;
        this.difficulty = difficulty;
    }


}


