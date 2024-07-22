package raihanhori.auction_api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import raihanhori.auction_api.entity.Category;
import raihanhori.auction_api.helper.ValidationHelper;
import raihanhori.auction_api.repository.CategoryRepository;
import raihanhori.auction_api.request.category.CreateCategoryRequest;
import raihanhori.auction_api.request.category.UpdateCategoryRequest;
import raihanhori.auction_api.response.CategoryResponse;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ValidationHelper validationHelper;

	@Override
	public List<CategoryResponse> getAll() {
		List<Category> categories = categoryRepository.findAll();

		return categories.stream()
				.map(category -> CategoryResponse.builder().id(category.getId()).name(category.getName()).build())
				.toList();
	}

	@Transactional
	@Override
	public void create(CreateCategoryRequest request) {
		validationHelper.validate(request);
		
		categoryRepository.findFirstByName(request.getName())
			.ifPresent((category) -> {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "category is already exist");
			});

		Category category = new Category();
		category.setName(request.getName());
		categoryRepository.save(category);
	}

	@Transactional
	@Override
	public void update(UpdateCategoryRequest request) {
		validationHelper.validate(request);

		Category category = categoryRepository.findById(request.getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "category not found"));
		
		categoryRepository.findFirstByNameExceptId(request.getName(), request.getId())
		.ifPresent((data) -> {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "category is already exist");
		});
		
		category.setName(request.getName());
		categoryRepository.save(category);
	}

	@Transactional
	@Override
	public void delete(Long categoryId) {
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "category not found"));
		
		categoryRepository.delete(category);
	}

}
