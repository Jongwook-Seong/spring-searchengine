package sjw.spring.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sjw.spring.domain.ArticleLink;
import sjw.spring.domain.user.User;

import java.util.*;

@Repository
@RequiredArgsConstructor
@Transactional
public class UserRepository {

    private final EntityManager em;

    public void saveUser(User user) {
        em.persist(user);
    }

    public User findOne(Long id) {
        return em.find(User.class, id);
    }

    public List<User> findAll() {
        return em.createQuery("select u from User u", User.class)
                .getResultList();
    }

    public List<User> findUserByLoginId(String loginId) {
        return em.createQuery("select u from User u where u.loginId = :loginId", User.class)
                .setParameter("loginId", loginId)
                .getResultList();
    }

    public List<User> findByName(String username) {
        return em.createQuery("select u from User u where u.username = :username", User.class)
                .setParameter("username", username)
                .getResultList();
    }

    public void saveArticle(ArticleLink articleLink) {
        em.persist(articleLink);
    }

    public List<ArticleLink> findArticleByUrl(String url) {
        return em.createQuery("select a from ArticleLink a where a.url = :url", ArticleLink.class)
                .setParameter("url", url)
                .getResultList();
    }
}