package gotcha_domain.gamehistory;

import gotcha_common.entity.BaseTimeEntity;
import gotcha_domain.report.BugReport;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private int score;

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


