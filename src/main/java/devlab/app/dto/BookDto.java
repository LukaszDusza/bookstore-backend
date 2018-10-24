package devlab.app.dto;


import devlab.app.model.Category;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class BookDto {

    private String title;
    private String isbn;
    private String author;
    private String category;
}
