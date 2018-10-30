package devlab.app.controller;


import devlab.app.commons.Mapper;
import devlab.app.dto.CategoryDto;
import devlab.app.mapper.CategoryMapper;
import devlab.app.model.Category;
import devlab.app.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/")
public class CategoryController {

    private CategoryRepository categoryRepository;
    private CategoryMapper mapper;


    public CategoryController(CategoryRepository categoryRepository, CategoryMapper mapper) {
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    @GetMapping("categories")
    public ResponseEntity<List<Category>> getCategories() {
        return new ResponseEntity<>(categoryRepository.findAll(), HttpStatus.OK);
    }


    @DeleteMapping("categories/{category}")
    public ResponseEntity<Category> deleteBook(@PathVariable("category") String category) {

        Optional<Category> categoryOptional = categoryRepository.findByTitle(category);

        if (categoryOptional.isPresent()) {
            categoryRepository.delete(categoryOptional.get());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
