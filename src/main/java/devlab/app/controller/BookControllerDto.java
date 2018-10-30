package devlab.app.controller;


import devlab.app.dto.BookDto;
import devlab.app.mapper.BookMapper;
import devlab.app.model.Book;
import devlab.app.model.Category;
import devlab.app.repository.BookRepository;
import devlab.app.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/")
public class BookControllerDto {


    private BookRepository bookRepository;
    private CategoryRepository categoryRepository;
    private BookMapper mapper;

    @Autowired /*nie wymagane*/
    public BookControllerDto(BookRepository bookRepository, CategoryRepository categoryRepository, BookMapper mapper) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    @GetMapping("books")
    public ResponseEntity<List<BookDto>> getBooks() {

        List<Book> books = bookRepository.findAll();
        List<BookDto> booksDto = new ArrayList<>();

       // books.forEach(book -> System.out.println(book.getAuthor()));

        for (Book b : books) {
            //  BookDto bookDto = mapper.map(b);
            //  booksDto.add(bookDto);
            booksDto.add(mapper.map(b));
        }
        return new ResponseEntity<>(booksDto, HttpStatus.OK);
    }


    @GetMapping("books/{isbn}")
    public ResponseEntity<BookDto> getBookByIsbn(@RequestParam(value = "isbn") String isbn) {

        Optional<Book> bookOpt = bookRepository.findByIsbn(isbn);

        if (bookOpt.isPresent()) {
            BookDto bookDto = mapper.map(bookOpt.get());

            return new ResponseEntity<>(bookDto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

//    @GetMapping("books/{author}")
//    public ResponseEntity<List<BookDto> getBookByAuthor(@RequestParam(value = "author") String author) {
//
//        Optional<Book> bookOpt = bookRepository.findByAuthor(author);
//
//        if (bookOpt.isPresent()) {
//            BookDto bookDto = mapper.map(bookOpt.get());
//
//            return new ResponseEntity<>(bookDto, HttpStatus.OK);
//        }
//        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//    }

    @GetMapping("books/{category}")
    public ResponseEntity<List<BookDto>> getBooksByCategory(@RequestParam(value = "category") String category) {

        Optional<Category> categoryOpt = categoryRepository.findByTitle(category);

        if (categoryOpt.isPresent()) {
            List<Book> books = bookRepository.findBooksByCategoryId(categoryOpt.get().getId());
            List<BookDto> bookDtos = new ArrayList<>();

            books.forEach(book -> {
                BookDto bookDto = mapper.map(book);
                bookDtos.add(bookDto);
            });

            return new ResponseEntity<>(bookDtos, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @PostMapping("books")
    public ResponseEntity<Book> addBook(@RequestBody BookDto bookDto) {

        if (bookRepository.findByIsbn(bookDto.getIsbn()).isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Optional<Category> categoryOptional = categoryRepository.findByTitle(bookDto.getCategory());

        if (categoryOptional.isPresent()) {

            Book book = new Book();
            book.setTitle(bookDto.getTitle());
            book.setIsbn(bookDto.getIsbn());
            book.setAuthor(bookDto.getAuthor());
            book.setCategory(categoryOptional.get());

            return new ResponseEntity<>(bookRepository.save(book), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @PutMapping("books")
    public ResponseEntity<Book> updateBook(@RequestParam("isbn") String isbn, @RequestBody BookDto bookDto) {

        Optional<Category> categoryOptional = categoryRepository.findByTitle(bookDto.getCategory());

        if (categoryOptional.isPresent()) {

            Optional<Book> bookOpt = bookRepository.findByIsbn(isbn);

            if (bookOpt.isPresent()) {

                bookOpt.get().setTitle(bookDto.getTitle());
                bookOpt.get().setAuthor(bookDto.getAuthor());
                bookOpt.get().setIsbn(bookDto.getIsbn());
                bookOpt.get().setCategory(categoryOptional.get());
                bookRepository.save(bookOpt.get());

                return new ResponseEntity<>(HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @DeleteMapping("books")
    public ResponseEntity<Book> deleteBook(@RequestParam("isbn") String isbn) {

        Optional<Book> bookOptional = bookRepository.findByIsbn(isbn);

        if (bookOptional.isPresent()) {
            bookRepository.delete(bookOptional.get());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
