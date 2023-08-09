package sjw.spring.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import sjw.spring.domain.user.User;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter @Setter
public class UserArticleLink {

    @Id @GeneratedValue
    @Column(name = "user_article_link_id")
    private Long id;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "article_link_id")
    private ArticleLink articleLink;
    private DisclosureScope scope;
}
