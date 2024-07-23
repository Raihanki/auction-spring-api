package raihanhori.auction_api.service;

import org.springframework.data.domain.Page;

import raihanhori.auction_api.entity.User;
import raihanhori.auction_api.request.product.CreateProductRequest;
import raihanhori.auction_api.request.product.GetProductRequest;
import raihanhori.auction_api.request.product.UpdateProductRequest;
import raihanhori.auction_api.response.ProductResponse;

public interface ProductService {
	
	Page<ProductResponse> getAll(GetProductRequest requests);
	
	void create(CreateProductRequest request);
	
	void update(UpdateProductRequest request, User user);
	
	ProductResponse detail(Long productId);
	
	void delete(Long productId, User user);
	
}
