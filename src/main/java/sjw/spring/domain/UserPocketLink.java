package sjw.spring.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import sjw.spring.domain.user.User;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
public class UserPocketLink {

    @Id
    @GeneratedValue
    @Column(name = "user_pocket_link_id")
    private Long id;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "article_link_id")
    private ArticleLink articleLink;
    private ReadStatus status;
}
