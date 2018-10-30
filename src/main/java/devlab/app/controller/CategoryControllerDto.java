package devlab.app.controller;


import devlab.app.dto.CategoryDto;
import devlab.app.mapper.CategoryMapper;
import devlab.app.model.Book;
import devlab.app.model.Category;
import devlab.app.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/")
public class CategoryControllerDto {

    private CategoryRepository categoryRepository;
    private CategoryMapper mapper;


    public CategoryControllerDto(CategoryRepository categoryRepository, CategoryMapper mapper) {
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    @GetMapping("categories")
    public ResponseEntity<List<CategoryDto>> getCategories() {

        List<Category> categories = categoryRepository.findAll();
        List<CategoryDto> categoryDtos = new ArrayList<>();

        for (Category c : categories) {
            categoryDtos.add(mapper.map(c));
        }

        return new ResponseEntity<>(categoryDtos, HttpStatus.OK);
    }


    @PostMapping("categories")
    public ResponseEntity<Category> addCategory(@RequestBody CategoryDto categoryDto) {
        Optional<Category> categoryOptional = categoryRepository.findByTitle(categoryDto.getTitle());

        if (categoryOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        Category category = new Category();
        category.setTitle(categoryDto.getTitle());
        category.setBooks(Collections.emptySet());
        return new ResponseEntity<>(categoryRepository.save(category), HttpStatus.OK);
    }

    @PutMapping("categories")
    public ResponseEntity<Category> updateCategory(@RequestParam String title, @RequestBody Category category) {
//        categoryRepository.findByTitle(title).ifPresent(c -> {
//            c.setTitle(category.getTitle());
//            categoryRepository.save(c);
//        });
        Optional<Category> categoryOptional = categoryRepository.findByTitle(title);
        if (categoryOptional.isPresent()) {
            categoryOptional.get().setTitle(category.getTitle());
            return new ResponseEntity<>(categoryRepository.save(categoryOptional.get()),HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("categories")
    public ResponseEntity<Category> deleteCategory(@RequestParam("category") String category) {

        Optional<Category> categoryOptional = categoryRepository.findByTitle(category);

        if (categoryOptional.isPresent()) {
            categoryRepository.delete(categoryOptional.get());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
