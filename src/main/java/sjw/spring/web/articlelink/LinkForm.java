package sjw.spring.web.articlelink;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import sjw.spring.domain.DisclosureScope;

@Getter @Setter
public class LinkForm {

    @NotEmpty(message = "링크를 입력해주세요.")
    private String url;
    private DisclosureScope scope;
}
