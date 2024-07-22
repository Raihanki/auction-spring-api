package raihanhori.auction_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import raihanhori.auction_api.helper.SuccessApiResponse;
import raihanhori.auction_api.request.category.CreateCategoryRequest;
import raihanhori.auction_api.request.category.UpdateCategoryRequest;
import raihanhori.auction_api.response.CategoryResponse;
import raihanhori.auction_api.service.CategoryService;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
	
	@Autowired
	private CategoryService categoryService;
	
	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public SuccessApiResponse<List<CategoryResponse>> index() {
		List<CategoryResponse> categories = categoryService.getAll();
		
		return SuccessApiResponse.<List<CategoryResponse>>builder()
					.status(200)
					.data(categories)
					.build();
	}
	
	@PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)
	public SuccessApiResponse<String> store(@RequestBody CreateCategoryRequest request) {
		categoryService.create(request);
		
		return SuccessApiResponse.<String>builder()
					.status(201)
					.data("category sucessfully created")
					.build();
	}
	
	@PatchMapping(path = "/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public SuccessApiResponse<String> update(@RequestBody UpdateCategoryRequest request, @PathVariable Long categoryId) {
		request.setId(categoryId);
		categoryService.update(request);
		
		return SuccessApiResponse.<String>builder()
					.status(200)
					.data("category sucessfully updated")
					.build();
	}
	
	@DeleteMapping(path = "/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public SuccessApiResponse<String> destroy(@PathVariable Long categoryId) {
		categoryService.delete(categoryId);
		
		return SuccessApiResponse.<String>builder()
					.status(200)
					.data("category sucessfully updated")
					.build();
	}
	
}
