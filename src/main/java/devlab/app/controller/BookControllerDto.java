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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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

        for (Book b : books) {
            //  BookDto bookDto = mapper.map(b);
            //  booksDto.add(bookDto);
            booksDto.add(mapper.map(b));
        }
        return new ResponseEntity<>(booksDto, HttpStatus.OK);
    }

    @PostMapping("books")
    public ResponseEntity<Book> addBook(@RequestBody BookDto bookDto) {

        if (bookRepository.findByIsbn(bookDto.getIsbn()).isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Optional<Category> categoryOptional = categoryRepository.findByTitle(bookDto.getCategory());

        if (categoryOptional.isPresent()) {


            //wymagany dodatkowy konstruktor do klasy Book.
//            Book book = new Book(
//                    bookDto.getTitle(),
//                    bookDto.getIsbn(),
//                    bookDto.getAuthor(),
//                    categoryOptional.get()
//                    );

            //bez dodawania oddzelnego konstruktora.

            Book book = new Book();
            book.setTitle(bookDto.getTitle());
            book.setIsbn(bookDto.getIsbn());
            book.setAuthor(bookDto.getAuthor());
            book.setCategory(categoryOptional.get());

            return new ResponseEntity<>(bookRepository.save(book), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

}
