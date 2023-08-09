package sjw.spring.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sjw.spring.domain.ArticleLink;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional
public class ArticleLinkRepository {

    private final EntityManager em;

    public void save(ArticleLink articlelink) {
        em.persist(articlelink);
    }

    public ArticleLink findOne(Long id) {
        return em.find(ArticleLink.class, id);
    }

    public List<ArticleLink> findAll() {
        return em.createQuery("select a from ArticleLink a", ArticleLink.class)
                .getResultList();
    }
}
