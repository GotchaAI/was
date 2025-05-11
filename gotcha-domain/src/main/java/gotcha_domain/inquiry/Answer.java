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
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Answer extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User writer;

    @Setter
    @OneToOne(mappedBy = "answer", fetch = FetchType.LAZY)
    private Inquiry inquiry;

    @Builder
    public Answer(String content, User writer){
        this.content = content;
        this.writer = writer;
    }
}
