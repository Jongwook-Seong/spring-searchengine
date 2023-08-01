package sjw.spring.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sjw.spring.domain.ArticleLink;

@Repository
@RequiredArgsConstructor
@Transactional
public class ArticleLinkRepository {

    private final EntityManager em;

    public void save(ArticleLink articlelink) {
        em.persist(articlelink);
    }
}
