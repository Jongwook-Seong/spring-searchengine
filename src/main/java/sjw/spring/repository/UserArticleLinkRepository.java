package sjw.spring.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sjw.spring.domain.UserArticleLink;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional
public class UserArticleLinkRepository {

    private final EntityManager em;

    public void save(UserArticleLink userArticleLink) {
        em.persist(userArticleLink);
    }

    public UserArticleLink findOne(Long id) {
        return em.find(UserArticleLink.class, id);
    }

    public List<UserArticleLink> findAll(Long userId) {
        return em.createQuery("select l from UserArticleLink l join l.user u where u.id = :userId", UserArticleLink.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
