package devlab.app.controller;


import devlab.app.model.Book;
import devlab.app.repository.BookRepository;
import devlab.app.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/")
public class BookController {


    private BookRepository bookRepository;
    private CategoryRepository categoryRepository;

    @Autowired /*nie wymagane*/
    public BookController(BookRepository bookRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
    }

        @GetMapping("books")
    public ResponseEntity<List<Book>> getBooks() {
        return new ResponseEntity<>(bookRepository.findAll(), HttpStatus.OK);
    }

    @PostMapping("books")
    public ResponseEntity<Book> addBook (
            @RequestParam(value = "category") String category,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "author") String author,
            @RequestParam(value = "isbn") String isbn ) {

        if (bookRepository.findByIsbn(isbn).isPresent()) {
            return new ResponseEntity<>( HttpStatus.CONFLICT);

        } else if (categoryRepository.findByTitle(category).isPresent()){

            //tworzenie oddzielnego konstruktora pod ten przypadek nie będzie wymagane.
            Book book = new Book();
            book.setTitle(title);
            book.setAuthor(author);
            book.setIsbn(isbn);
            book.setCategory(categoryRepository.findByTitle(category).get());

            return new ResponseEntity<>(bookRepository.save(book), HttpStatus.OK);
        }
           return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @PutMapping("books")
    public ResponseEntity<Book> updateBook(@RequestParam String isbn, @RequestBody Book book) {
        Optional<Book> bookOptional = bookRepository.findByIsbn(isbn);

        if (bookOptional.isPresent()) {
            bookOptional.get().setTitle(book.getTitle());
            bookOptional.get().setAuthor(book.getAuthor());
            bookOptional.get().setIsbn(book.getIsbn());
            //  bookOptional.get().setId(book.getId()); /*nie poprawnie*/

            return new ResponseEntity<>(bookRepository.save(bookOptional.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("books/{isbn}")
    public ResponseEntity<Book> deleteBook(@PathVariable("isbn") String isbn) {

        Optional<Book> bookOptional = bookRepository.findByIsbn(isbn);

        if (bookOptional.isPresent()) {
            bookRepository.delete(bookOptional.get());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
