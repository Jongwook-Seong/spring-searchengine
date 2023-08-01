package sjw.spring.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import sjw.spring.domain.UserArticleLink;
import sjw.spring.domain.UserPocketLink;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter @Setter
public class User {

    @Id @GeneratedValue
    @Column(name = "USER_ID")
    private Long id;
    @Column(unique = true)
    private String loginId;
    private String username;
    private String password;

    @OneToMany(mappedBy = "user")
    private List<UserArticleLink> userLinks = new ArrayList<>();
    @OneToMany(mappedBy = "user")
    private List<UserPocketLink> pocketLinks = new ArrayList<>();
}
