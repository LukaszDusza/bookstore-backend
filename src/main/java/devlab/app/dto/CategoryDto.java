package devlab.app.dto;


import devlab.app.model.Book;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CategoryDto {

    private String title;
    private List<String> books;

}
