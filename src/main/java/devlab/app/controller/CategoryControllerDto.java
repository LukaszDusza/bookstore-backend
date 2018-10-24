package devlab.app.controller;


import devlab.app.dto.CategoryDto;
import devlab.app.mapper.CategoryMapper;
import devlab.app.model.Category;
import devlab.app.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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

        for (Category c: categories) {
            categoryDtos.add(mapper.map(c));
        }

        return new ResponseEntity<>(categoryDtos, HttpStatus.OK);
    }
}
