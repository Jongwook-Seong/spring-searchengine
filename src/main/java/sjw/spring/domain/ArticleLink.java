package sjw.spring.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class ArticleLink {

    @Id @GeneratedValue
    @Column(name = "articlelink_id")
    private Long id;
    private String url;
    private String title;
    @Column(length = 50000)
    private String content;
    private LocalDateTime sharedDate;
    private int viewCount;
}
