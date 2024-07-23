package raihanhori.auction_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import raihanhori.auction_api.entity.User;
import raihanhori.auction_api.helper.DataPaginationResponse;
import raihanhori.auction_api.helper.PaginationData;
import raihanhori.auction_api.helper.SuccessApiResponse;
import raihanhori.auction_api.request.product.CreateProductRequest;
import raihanhori.auction_api.request.product.GetProductRequest;
import raihanhori.auction_api.request.product.UpdateProductRequest;
import raihanhori.auction_api.response.ProductResponse;
import raihanhori.auction_api.service.ProductService;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
	
	@Autowired
	private ProductService productService;
	
	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public DataPaginationResponse<List<ProductResponse>> index(
				@RequestParam(value = "name", required = false) String name,
				@RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
				@RequestParam(value = "page", required = false, defaultValue = "1") Integer page
	) {
		GetProductRequest request = GetProductRequest.builder()
				.search(name).limit(limit).page(page)
				.build();
		
		Page<ProductResponse> products = productService.getAll(request);
		
		PaginationData paginationData = PaginationData.builder()
				.current_page(products.getNumber() + 1)
				.total_page(products.getTotalPages())
				.total_item(products.getTotalElements())
				.total_item_per_page(products.getSize())
				.build();
		
		return DataPaginationResponse.<List<ProductResponse>>builder()
				.status(200)
				.data(products.getContent())
				.meta(paginationData)
				.build();
		
	}
	
	@ResponseStatus(code = HttpStatus.CREATED)
	@PostMapping(path = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public SuccessApiResponse<String> store(@ModelAttribute CreateProductRequest request, BindingResult result) {
		if (result.hasErrors()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, result.getFieldErrors().toString());
		}
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		request.setUserId(user.getId());
		
		productService.create(request);
		
		return SuccessApiResponse.<String>builder()
				.status(201)
				.data("product created successflully")
				.build();
	}
	
	
	@PostMapping(path = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public SuccessApiResponse<String> update(@ModelAttribute UpdateProductRequest request, @PathVariable(name = "productId") Long productId, BindingResult result) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if (result.hasErrors()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, result.getFieldErrors().toString());
		}
		
		request.setId(productId);
		request.setUserId(user.getId());
		
		productService.update(request, user);
		
		return SuccessApiResponse.<String>builder()
				.status(200)
				.data("product updated successfully")
				.build();
	}
	
	@DeleteMapping(path = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public SuccessApiResponse<String> destroy(@PathVariable(name = "productId") Long productId) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		productService.delete(productId, user);
		
		return SuccessApiResponse.<String>builder()
				.status(200)
				.data("product updated successfully")
				.build();
	
	}
	
	@GetMapping(path = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public SuccessApiResponse<ProductResponse> show(@PathVariable(name = "productId") Long productId) {
		ProductResponse product = productService.detail(productId);
		
		return SuccessApiResponse.<ProductResponse>builder()
				.status(200)
				.data(product)
				.build();
	
	}
}
