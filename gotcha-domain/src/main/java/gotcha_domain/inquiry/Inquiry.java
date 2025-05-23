package gotcha_domain.inquiry;

import gotcha_common.entity.BaseTimeEntity;
import gotcha_domain.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String title;

    @NotNull
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User writer;

    private Boolean isSecret;

    private Boolean isSolved = false;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id")
    private Answer answer;

    @Builder
    public Inquiry(String title, String content, Boolean isSecret, User writer){
        this.title = title;
        this.content = content;
        this.isSecret = isSecret;
        this.writer = writer;
    }

    public void update(String title, String content, Boolean isPrivate){
        this.title = title;
        this.content = content;
        this.isSecret = isPrivate;
    }

    public void solve(Answer answer){
        if(validateSolved()){
            this.answer = answer;
            isSolved = true;
            answer.setInquiry(this);
        }
    }

    private boolean validateSolved(){
        return isSolved;
    }
}
