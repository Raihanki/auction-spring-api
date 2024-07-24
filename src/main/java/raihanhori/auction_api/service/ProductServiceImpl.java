package raihanhori.auction_api.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import raihanhori.auction_api.entity.Category;
import raihanhori.auction_api.entity.Currency;
import raihanhori.auction_api.entity.Image;
import raihanhori.auction_api.entity.Product;
import raihanhori.auction_api.entity.User;
import raihanhori.auction_api.helper.ValidationHelper;
import raihanhori.auction_api.repository.CategoryRepository;
import raihanhori.auction_api.repository.ProductRepository;
import raihanhori.auction_api.repository.UserRepository;
import raihanhori.auction_api.request.product.CreateProductRequest;
import raihanhori.auction_api.request.product.GetProductRequest;
import raihanhori.auction_api.request.product.UpdateProductRequest;
import raihanhori.auction_api.response.CategoryResponse;
import raihanhori.auction_api.response.ImageResponse;
import raihanhori.auction_api.response.ProductResponse;
import raihanhori.auction_api.response.UserResponse;

@Service
public class ProductServiceImpl implements ProductService {
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ValidationHelper validationHelper;
	
	@Autowired
	private ImageService imageService;

	@Override
	public Page<ProductResponse> getAll(GetProductRequest request) {
		int page = (request.getPage() != null) ? request.getPage() - 1 : 0;
		int limit = (request.getLimit() != null) ? request.getLimit() : 10;
		Pageable pageable = PageRequest.of(page, limit, Sort.by("id").ascending());
		
		Page<Product> products = 
				productRepository.getAndSearchProduct(request.getSearch(), pageable);
		
		List<ProductResponse> productList = products.stream().map(product -> toProductResponse(product)).toList();
		
		return new PageImpl<ProductResponse>(productList, pageable, products.getTotalElements());
	}

	@Transactional
	@Override
	public void create(CreateProductRequest request) {
		validationHelper.validate(request);
		
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		Category category = categoryRepository.findById(request.getCategoryId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "category not found"));
		
		if (!"IDR".equals(request.getCurrency()) && !"USD".equals(request.getCurrency())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "currency is valid only for IDR and USD");
		}
		
		if (request.getEndAuctionDate().before(new Date(System.currentTimeMillis()))) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "end auction time should be after start date");
		}
		
		if (request.getImages().length > 3) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "maximum file upload is only 3");
		}
		
		Product product = new Product();
		product.setOwner(user);
		product.setCategory(category);
		product.setName(request.getName());
		product.setDescription(request.getDescription());
		product.setStartPrice(request.getStartPrice());
		product.setPriceMultiples(request.getPriceMultiples());
		product.setEndPrice(request.getStartPrice());
		product.setCurrency(Currency.valueOf(request.getCurrency()));
		product.setEndAuctionDate(new Timestamp(request.getEndAuctionDate().getTime()));
		productRepository.save(product);
		
		List<Image> images = new ArrayList<Image>();
		Arrays.asList(request.getImages()).stream().forEach(file -> {
			String filePath = imageService.savePublic(file);
			images.add(Image.builder().product(product).imageUrl(filePath).build());
		});
		
		images.forEach(image -> imageService.saveDatabase(image));
		product.setImages(images);
	}

	@Transactional
	@Override
	public void update(UpdateProductRequest request, User user) {
		validationHelper.validate(request);
		
		Product product = productRepository.findById(request.getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product not found"));
		
		if (!product.getOwner().getId().equals(user.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "cannot update this product");
		}
		
		User userWinner;
		if (request.getWinnerUserId() != null) {
			userWinner = userRepository.findById(request.getWinnerUserId())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user winner not found"));
		} else {
			userWinner = null;
		}
		
		Category category = categoryRepository.findById(request.getCategoryId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "category not found"));
		
		if (!"IDR".equals(request.getCurrency()) && !"USD".equals(request.getCurrency())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "currency is valid only for IDR and USD");
		}
		
		if (request.getEndAuctionDate().toInstant().isBefore(product.getCreatedAt())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "end auction time should be after start date");
		}
		
		if (request.getImages().length > 3) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "maximum file upload is only 3");
		}
		
		product.setOwner(user);
		product.setCategory(category);
		product.setName(request.getName());
		product.setDescription(request.getDescription());
		product.setStartPrice(request.getStartPrice());
		product.setPriceMultiples(request.getPriceMultiples());
		product.setEndPrice(request.getEndPrice());
		product.setCurrency(Currency.valueOf(request.getCurrency()));
		product.setEndAuctionDate(new Timestamp(request.getEndAuctionDate().getTime()));
		product.setWinnerUser(userWinner);
		productRepository.save(product);
		
		imageService.getImagesByProductId(product.getId()).stream().forEach(image -> {
			imageService.delete(image.getImageUrl());
			imageService.deleteDatabase(image);
		});
		
		List<Image> images = new ArrayList<Image>();
		Arrays.asList(request.getImages()).stream().forEach(file -> {
			String filePath = imageService.savePublic(file);
			images.add(Image.builder().product(product).imageUrl(filePath).build());
		});
		
		images.forEach(image -> imageService.saveDatabase(image));
		product.setImages(images);
	}

	@Transactional
	@Override
	public ProductResponse detail(Long productId) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product not found"));
		
		return this.toProductResponse(product);
	}

	
	@Transactional
	@Override
	public void delete(Long productId, User user) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product not found"));
		
		if (!product.getOwner().getId().equals(user.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "cannot update this product");
		}
		
		 imageService.getImagesByProductId(productId).stream().forEach(image -> {
		 		imageService.delete(image.getImageUrl());
		 		imageService.deleteDatabase(image);
		 });
		
		productRepository.delete(product);
	}
	
	private ProductResponse toProductResponse(Product product) {
		
		User user = product.getOwner();
		UserResponse userResponse = UserResponse.builder().id(user.getId()).name(user.getName()).email(user.getEmail()).createdAt(user.getCreatedAt()).build();
		
		User winnerUser = product.getWinnerUser();
		UserResponse winnerUserResponse = new UserResponse();
		if (winnerUser != null) {
			winnerUserResponse = UserResponse.builder().id(winnerUser.getId()).name(winnerUser.getName()).email(winnerUser.getEmail()).createdAt(winnerUser.getCreatedAt()).build();
		}
		
		Category category = product.getCategory();
		
		List<ImageResponse> images = new ArrayList<ImageResponse>();
		product.getImages().stream().forEach(image -> {
			images.add(ImageResponse.builder().id(image.getId()).imageUrl(image.getImageUrl()).build());
		});
		
		return ProductResponse.builder()
			.id(product.getId())
			.user(userResponse)
			.category(CategoryResponse.builder().id(category.getId()).name(category.getName()).build())
			.winnerUser(winnerUserResponse)
			.name(product.getName())
			.description(product.getDescription())
			.startPrice(product.getStartPrice())
			.priceMultiples(product.getPriceMultiples())
			.endPrice(product.getEndPrice())
			.currency(product.getCurrency())
			.endAuctionDate(product.getEndAuctionDate())
			.createdAt(product.getCreatedAt())
			.images(images)
			.build();
	}

}
