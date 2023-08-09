package sjw.spring.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sjw.spring.domain.ArticleLink;
import sjw.spring.repository.ArticleLinkRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleLinkService {

    private final ArticleLinkRepository articleLinkRepository;

    public List<ArticleLink> findAll() {
        return articleLinkRepository.findAll();
    }
}
