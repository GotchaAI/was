package Gotcha.domain.game.entity;

import Gotcha.common.entity.BaseTimeEntity;
import Gotcha.domain.report.entity.BugReport;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
public class Game extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private GameType gameType;

    @NotNull
    private Difficulty difficulty;

    @OneToMany(mappedBy = "game")
    private List<BugReport> bugReports = new ArrayList<>();

    @OneToMany(mappedBy = "game")
    private List<UserGame> userGames = new ArrayList<>();

    @OneToMany(mappedBy = "game")
    private List<RoundHistory> roundHistories = new ArrayList<>();

    @Builder
    public Game(GameType gameType, Difficulty difficulty){
        this.gameType = gameType;
        this.difficulty = difficulty;
    }
}
