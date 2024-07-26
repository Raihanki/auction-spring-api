package raihanhori.auction_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import raihanhori.auction_api.entity.User;
import raihanhori.auction_api.helper.DataPaginationResponse;
import raihanhori.auction_api.helper.PaginationData;
import raihanhori.auction_api.helper.SuccessApiResponse;
import raihanhori.auction_api.request.auction.GetAuctionRequest;
import raihanhori.auction_api.request.product.GetProductRequest;
import raihanhori.auction_api.response.MyAuctionResponse;
import raihanhori.auction_api.response.ProductResponse;
import raihanhori.auction_api.response.UserResponse;
import raihanhori.auction_api.service.AuctionService;
import raihanhori.auction_api.service.ProductService;
import raihanhori.auction_api.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private AuctionService auctionService;
	
	@Autowired
	private ProductService productService;
	
	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public SuccessApiResponse<UserResponse> show() {
		UserResponse response = userService.getUser();
		
		return SuccessApiResponse.<UserResponse>builder()
				.status(200).data(response)
				.build();
	}
	
	@GetMapping(path = "/myBid", produces = MediaType.APPLICATION_JSON_VALUE)
	public DataPaginationResponse<List<MyAuctionResponse>> myAuctions(
			@RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer page
		) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		GetAuctionRequest request = GetAuctionRequest.builder()
				.limit(limit)
				.page(page)
				.build();
		
		Page<MyAuctionResponse> auctions = auctionService.getMyAuction(request, user);
		
		PaginationData paginationData = PaginationData.builder()
				.current_page(auctions.getNumber() + 1)
				.total_page(auctions.getTotalPages())
				.total_item(auctions.getTotalElements())
				.total_item_per_page(auctions.getSize())
				.build();
		
		return DataPaginationResponse.<List<MyAuctionResponse>>builder()
				.status(200)
				.data(auctions.getContent())
				.meta(paginationData)
				.build();
	}
	
	@GetMapping(path = "/myProducts", produces = MediaType.APPLICATION_JSON_VALUE)
	public DataPaginationResponse<List<ProductResponse>> index(
				@RequestParam(value = "name", required = false) String name,
				@RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
				@RequestParam(value = "page", required = false, defaultValue = "1") Integer page
	) {
		GetProductRequest request = GetProductRequest.builder()
				.search(name).limit(limit).page(page)
				.build();
		
		Page<ProductResponse> products = productService.getMyProduct(request);
		
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
	
}
