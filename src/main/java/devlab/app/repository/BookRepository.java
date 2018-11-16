package devlab.app.repository;

import devlab.app.dto.BookDto;
import devlab.app.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    String BY_AUTHOR = "SELECT * FROM books WHERE author like (?1%)";
    //  String BY_CATEGORY = "SELECT * FROM categories WHERE title like ?1%";
    String AUTHORS = "SELECT distinct author FROM books";


    Optional<Book> findByIsbn(String isbn);

    List<Book> findBooksByCategoryId(Long fk_category);

    // @Async
    @Query(value = BY_AUTHOR, nativeQuery = true)
    Optional<List<Book>> findByAuthor(String author);

    //  @Async
    //  @Query(value = BY_CATEGORY, nativeQuery = true)
    //  Optional<Book> findByCategory(String category);

    @Query(value = AUTHORS, nativeQuery = true)
    List<String> getAuthorsNames();

}
