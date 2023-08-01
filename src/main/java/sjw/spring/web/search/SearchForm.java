package sjw.spring.web.search;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SearchForm {

    @NotEmpty
    private String searchQuery;
}
