package devlab.app.mapper;

import devlab.app.commons.Mapper;
import devlab.app.dto.BookDto;
import devlab.app.dto.CategoryDto;
import devlab.app.model.Book;
import devlab.app.model.Category;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CategoryMapper implements Mapper<Category, CategoryDto> {


    @Override
    public CategoryDto map(Category from) {

        List<String> books = from.getBooks()
                .stream()
                .map(BooksToString.INSTANCE)
                .collect(Collectors.toList());

        return new CategoryDto (
                from.getTitle(),
                books
        );

   }

    private enum BooksToString implements Function<Book, String> {
        INSTANCE;

        @Override
        public String apply(Book book) {
            return book.getTitle();
        }
    }
}
