package sjw.spring.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class ArticleLink {

    @Id @GeneratedValue
    @Column(name = "articlelink_id")
    private Long id;
    private String url;
    private String title;
    @Column(length = 10000)
    private String content;
    private LocalDateTime sharedDate;
    private int viewCount;
}
