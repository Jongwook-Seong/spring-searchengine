package sjw.spring.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sjw.spring.domain.ArticleLink;
import sjw.spring.domain.DisclosureScope;
import sjw.spring.domain.UserArticleLink;
import sjw.spring.domain.user.User;
import sjw.spring.repository.UserArticleLinkRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserArticleLinkService {

    private final UserArticleLinkRepository userArticleLinkRepository;

    public List<UserArticleLink> getList(Long userId) {

        List<UserArticleLink> links = userArticleLinkRepository.findAll(userId);

        return links;
    }

    public Long post(String url, String title, String content, User loginUser, DisclosureScope scope) {

        ArticleLink aLink = new ArticleLink();
        aLink.setUrl(url);
        aLink.setTitle(title);
        aLink.setContent(content);
        aLink.setSharedDate(LocalDateTime.now());
        aLink.setViewCount(0);

        UserArticleLink uaLink = new UserArticleLink();
        uaLink.setUser(loginUser);
        uaLink.setArticleLink(aLink);
        uaLink.setScope(scope);

        userArticleLinkRepository.save(uaLink);

        return uaLink.getId();
    }
}
