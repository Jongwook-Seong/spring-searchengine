package sjw.spring.web.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ArticleCrawler {

    private String url;
    private String textTitle;
    private String textContent;

    public ArticleCrawler() {
    }

    public ArticleCrawler(String url) {
        this.url = url;
    }

    public void setTextFromUrl(String url) throws IOException {

        if (this.url == null) {
            this.url = url;
        }

        // Element 구조 특성에 의해 매일경제 기사에 한해서만 내용을 가져올 수 있다.
        Document doc = Jsoup.connect(url).get();
        Element docTitle = doc.select("h2.news_ttl").first();
        Elements docContents = doc.select("div.news_cnt_detail_wrap p");

        textTitle = docTitle.text();
        textContent = "";
        for (Element docContent : docContents) {
            textContent += docContent.text() + " ";
        }
    }



    public String getTextTitle() {
        return textTitle;
    }

    public String getTextContent() {
        return textContent;
    }
}
