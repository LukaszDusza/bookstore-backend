package devlab.app.controller;


import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import devlab.app.dto.BookDto;
import devlab.app.mapper.BookMapper;
import devlab.app.model.Book;
import devlab.app.model.Category;
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
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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


    public List<BookDto> getBooksList() {
        List<Book> books = bookRepository.findAll();
        List<BookDto> booksDto = new ArrayList<>();

//        for (Book b : books) {
//            booksDto.add(mapper.map(b));
//        }

        books.forEach(b -> booksDto.add(mapper.map(b)));

        return booksDto;
    }


    @GetMapping("books{excel_file_name}")
    public void createXLSXSheet(@RequestParam(value = "excel_file_name") String excel_file_name) {

        List<BookDto> books = getBooksList();

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(excel_file_name);

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

        Date date = new Date();
        long time = date.getTime();

        try {
            FileOutputStream fos = new FileOutputStream(excel_file_name + "_" + time + ".xlsx");
            workbook.write(fos);

            workbook.close();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @GetMapping("books{isbn}")
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

    @GetMapping("books{category}")
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


    @PostMapping("upload")
    public void openXLSFile(@RequestParam("file") MultipartFile file) throws IOException {

        InputStream inputStream = new BufferedInputStream(file.getInputStream());
        HSSFWorkbook workbook = new HSSFWorkbook(inputStream);

        Sheet sheet = workbook.getSheetAt(0);

//        for(int collIndex = 0; collIndex < 4; collIndex++) {
//            for(int rowIndex = 0; rowIndex < sheet.getLastRowNum(); rowIndex++) {
//                Cell cell = sheet.getRow(rowIndex).getCell(collIndex);
//                System.out.println(cell);
//            }
//        }

        List<BookDto> books = new ArrayList<>();
        for (int rowIndex = 1; rowIndex < sheet.getLastRowNum(); rowIndex++) {

            List<String> props = new ArrayList<>();

            for (int collIndex = 0; collIndex < 4; collIndex++) {

                Cell cell = sheet.getRow(rowIndex).getCell(collIndex);
                props.add(cell.toString());
                //   System.out.println(cell);
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

     //   books.forEach(System.out::println);

        addBooks(books);

      //  return books;
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
