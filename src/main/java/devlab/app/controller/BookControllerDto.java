package devlab.app.controller;

import devlab.app.dto.BookDto;
import devlab.app.mapper.BookMapper;
import devlab.app.model.Book;
import devlab.app.model.Category;
import devlab.app.model.MyFile;
import devlab.app.repository.BookRepository;
import devlab.app.repository.CategoryRepository;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/")
public class BookControllerDto {

    private static String UPLOADED_FOLDER = new File("").getAbsolutePath() + "//uploads//";


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
    public ResponseEntity<?> getBooks(
            @RequestParam(value = "isbn", required = false) String isbn,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "author", required = false) String author) {

        if (isbn != null) {
            return getBookByIsbn(isbn);
        } else if (category != null) {
            return getBooksByCategory(category);
        } else if (author != null) {
            return new ResponseEntity<>(getBooksByAuthor(author), HttpStatus.OK);
        }
        return new ResponseEntity<>(getAllBooks(), HttpStatus.OK);
    }

    private List<BookDto> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        List<BookDto> booksDto = new ArrayList<>();
        for (Book b : books) {
            booksDto.add(mapper.map(b));
        }
        return booksDto;
    }

    private ResponseEntity<BookDto> getBookByIsbn(String isbn) {
        Optional<Book> bookOpt = bookRepository.findByIsbn(isbn);
        if (bookOpt.isPresent()) {
            BookDto bookDto = mapper.map(bookOpt.get());
            return new ResponseEntity<>(bookDto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private List<BookDto> getBooksByAuthor(String author) {

        Optional<List<Book>> books = bookRepository.findByAuthor(author);
        List<BookDto> bookDtos = new ArrayList<>();
        books.ifPresent(book -> book.forEach(b -> bookDtos.add(mapper.map(b))));

        return bookDtos;
    }

    private ResponseEntity<List<BookDto>> getBooksByCategory(String category) {

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

    @GetMapping("books/authors")
    public List<String> getAuthors() {
        return bookRepository.getAuthorsNames();

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


    @DeleteMapping("books/{isbn}")
    public ResponseEntity<Book> deleteBook(@PathVariable("isbn") String isbn) {

        Optional<Book> bookOptional = bookRepository.findByIsbn(isbn);

        if (bookOptional.isPresent()) {
            bookRepository.delete(bookOptional.get());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    private List<BookDto> getBooksList() {

        List<Book> books = bookRepository.findAll();
        List<BookDto> booksDto = new ArrayList<>();
        books.forEach(b -> booksDto.add(mapper.map(b)));

        return booksDto;
    }


    @PostMapping(value = "books/file/add")
    public MyFile createXLS(@RequestBody List<BookDto> books) {

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("books");

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 10);
        headerFont.setColor(IndexedColors.BLACK.getIndex());

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        String[] columns = {"Title", "Author", "ISBN", "Category"};

        Row headerRow = sheet.createRow(0);

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        AtomicInteger counter = new AtomicInteger();

        books.forEach(b -> {

            counter.getAndIncrement();

            HSSFRow row = sheet.createRow(counter.get());

            HSSFCell cell1 = row.createCell(0);
            HSSFCell cell2 = row.createCell(1);
            HSSFCell cell3 = row.createCell(2);
            HSSFCell cell4 = row.createCell(3);

            cell1.setCellValue(b.getTitle());
            cell2.setCellValue(b.getAuthor());
            cell3.setCellValue(b.getIsbn());
            cell4.setCellValue(b.getCategory());
        });


        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }


        long time = System.currentTimeMillis();
        String file = UPLOADED_FOLDER + "books" + time + ".xls";

        try {
         //   FileOutputStream fos = new FileOutputStream(fileName + ".xls");
        //    workbook.write(fos);

            byte[] bytes = workbook.getBytes();
            Path path = Paths.get(file);
            Files.write(path, bytes);

            workbook.close();
         //   fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        File newFile = new File(UPLOADED_FOLDER + file);

        return new MyFile(newFile.getName(), file);

    }


    @PostMapping("books/file/open")
    public List<BookDto> openXLSFile(@RequestParam("upload") MultipartFile upload) throws IOException {

        InputStream inputStream = new BufferedInputStream(upload.getInputStream());
        HSSFWorkbook workbook = new HSSFWorkbook(inputStream);

        Sheet sheet = workbook.getSheetAt(0);

        List<BookDto> books = new ArrayList<>();
        for (int rowIndex = 1; rowIndex < sheet.getPhysicalNumberOfRows(); rowIndex++) {

            List<String> props = new ArrayList<>();

            for (int collIndex = 0; collIndex < 4; collIndex++) {

                Cell cell = sheet.getRow(rowIndex).getCell(collIndex);
                props.add(cell.toString());
                //    System.out.println(cell);
            }
            BookDto book = new BookDto(
                    props.get(0),
                    props.get(1),
                    props.get(2),
                    props.get(3)
            );
            books.add(book);
        }
        workbook.close();
        inputStream.close();

        books.forEach(System.out::println);

        addBooks(books);

        return books;
    }


    public void addBooks(List<BookDto> bookDtos) {

        List<Book> books = new ArrayList<>();

        bookDtos.forEach(bd -> {
            Optional<Category> cat = categoryRepository.findByTitle(bd.getCategory());

            cat.ifPresent(category -> books.add(
                    new Book(
                            bd.getTitle(),
                            bd.getIsbn(),
                            bd.getAuthor(),
                            category
                    )
            ));
        });

        bookRepository.saveAll(books);
    }
}
