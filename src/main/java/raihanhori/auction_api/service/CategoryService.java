package raihanhori.auction_api.service;

import java.util.List;

import raihanhori.auction_api.request.category.CreateCategoryRequest;
import raihanhori.auction_api.request.category.UpdateCategoryRequest;
import raihanhori.auction_api.response.CategoryResponse;

public interface CategoryService {

	List<CategoryResponse> getAll();
	
	void create(CreateCategoryRequest request);
	
	void update(UpdateCategoryRequest request);
	
	void delete(Long categoryId);
	
}
