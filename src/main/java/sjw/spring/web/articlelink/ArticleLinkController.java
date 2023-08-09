package sjw.spring.web.articlelink;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import sjw.spring.domain.ArticleLink;
import sjw.spring.repository.ArticleLinkRepository;
import sjw.spring.web.searchengine.SearchForm;
import sjw.spring.web.searchengine.Searcher;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ArticleLinkController {

    private final ArticleLinkRepository articleLinkRepository;

    @PostMapping("/links/search")
    public String searchList(SearchForm form, Model model) throws IOException {

        List<ArticleLink> articleLinks = engineSearch(form.getSearchQuery());

        model.addAttribute("links", articleLinks);

        return "links/searchResultLinks";
    }

    public List<ArticleLink> engineSearch(String query) throws IOException {

        List<ArticleLink> links = articleLinkRepository.findAll();
        Searcher searcher = new Searcher("D:\\Spring Projects\\spring\\");
        List<ArticleLink> searchedResultList = searcher.Searching(query, links, links.size());
        return searchedResultList;
    }
}
