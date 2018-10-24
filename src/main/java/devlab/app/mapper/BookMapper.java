package devlab.app.mapper;


import devlab.app.commons.Mapper;
import devlab.app.dto.BookDto;
import devlab.app.model.Book;
import devlab.app.model.Category;
import org.springframework.stereotype.Component;

@Component
public class BookMapper implements Mapper<Book, BookDto> {


    @Override
    public BookDto map(Book from) {
        return new BookDto(
                from.getTitle(),
                from.getIsbn(),
                from.getAuthor(),
                from.getCategory().getTitle()
        );
    }

}
