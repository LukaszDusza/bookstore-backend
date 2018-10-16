package devlab.app.controller;


import devlab.app.model.Book;
import devlab.app.repository.BookRepository;
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

    @Autowired /*nie wymagane*/
    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

        @GetMapping("books")
    public ResponseEntity<List<Book>> getBooks() {
        return new ResponseEntity<>(bookRepository.findAll(), HttpStatus.OK);
    }

    @PostMapping("books")
    public ResponseEntity<Book> addBook(@RequestBody Book book) {

        if (bookRepository.findByIsbn(book.getIsbn()).isPresent()) {
            return new ResponseEntity<>(book, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(bookRepository.save(book), HttpStatus.OK);
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
