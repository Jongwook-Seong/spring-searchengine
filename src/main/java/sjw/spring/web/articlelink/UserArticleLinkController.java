package sjw.spring.web.articlelink;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import sjw.spring.domain.ArticleLink;
import sjw.spring.domain.UserArticleLink;
import sjw.spring.domain.service.ArticleLinkService;
import sjw.spring.domain.service.UserArticleLinkService;
import sjw.spring.domain.user.User;
import sjw.spring.web.SessionConst;
import sjw.spring.web.crawler.ArticleCrawler;
import sjw.spring.web.searchengine.Indexer;

import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserArticleLinkController {

    private final UserArticleLinkService userArticleLinkService;
    private final ArticleLinkService articleLinkService;

    @GetMapping("/links")
    public String list(Model model, HttpServletRequest request) {

        HttpSession session = request.getSession();
        User loginUser = (User) session.getAttribute(SessionConst.LOGIN_USER);

        List<UserArticleLink> links = userArticleLinkService.getList(loginUser.getId());

        model.addAttribute("links", links);

        return "links/myLinks";
    }

    @GetMapping("/links/new")
    public String postLinkForm(Model model) {

        model.addAttribute("form", new LinkForm());
        return "links/postLinkForm";
    }

    @PostMapping("/links/new")
    public String postLink(LinkForm form, HttpServletRequest request) throws IOException {

        String url = form.getUrl();

        // 입력된 URL의 내용을 크롤링한다.
        ArticleCrawler articleCrawler = new ArticleCrawler();
        articleCrawler.setTextFromUrl(url);

        // 현재 로그인 유저의 객체 정보를 받는다.
        HttpSession session = request.getSession();
        User loginUser = (User) session.getAttribute(SessionConst.LOGIN_USER);

        userArticleLinkService.post(url, articleCrawler.getTextTitle(), articleCrawler.getTextContent(), loginUser, form.getScope());

        // 검색엔진 인덱스
        engineIndex();

        return "redirect:/links";
    }

    public void engineIndex() throws IOException {

        List<ArticleLink> aLinks = articleLinkService.findAll();

        Indexer indexer = new Indexer(aLinks, aLinks.size());
        indexer.Indexing();
    }
}
