package raihanhori.auction_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import raihanhori.auction_api.entity.User;
import raihanhori.auction_api.helper.DataPaginationResponse;
import raihanhori.auction_api.helper.PaginationData;
import raihanhori.auction_api.helper.SuccessApiResponse;
import raihanhori.auction_api.request.auction.CreateAuctionRequest;
import raihanhori.auction_api.request.auction.GetAuctionRequest;
import raihanhori.auction_api.response.AuctionResponse;
import raihanhori.auction_api.service.AuctionService;

@RestController
@RequestMapping(path = "/api/v1/products/{productId}/auctions")
public class AuctionController {
	
	@Autowired
	private AuctionService auctionService;

	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public DataPaginationResponse<List<AuctionResponse>> index(
			@PathVariable("productId") Long productId,
			@RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer page
		) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		GetAuctionRequest request = GetAuctionRequest.builder()
				.limit(limit)
				.page(page)
				.build();
		
		Page<AuctionResponse> auctions = auctionService.getAll(request, productId, user);
		
		PaginationData paginationData = PaginationData.builder()
				.current_page(auctions.getNumber() + 1)
				.total_page(auctions.getTotalPages())
				.total_item(auctions.getTotalElements())
				.total_item_per_page(auctions.getSize())
				.build();
		
		return DataPaginationResponse.<List<AuctionResponse>>builder()
				.status(200)
				.data(auctions.getContent())
				.meta(paginationData)
				.build();
	}
	
	@ResponseStatus(code = HttpStatus.CREATED)
	@PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public SuccessApiResponse<String> store(@RequestBody CreateAuctionRequest request, @PathVariable("productId") Long productId) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		request.setProductId(productId);
		auctionService.create(request, user);
		
		return SuccessApiResponse.<String>builder()
				.status(201)
				.data("successfully created")
				.build();
	}
	
	@DeleteMapping(path = "/{auctionId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public SuccessApiResponse<String> destroy(@PathVariable("productId") Long productId, @PathVariable("auctionId") Long auctionId) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		auctionService.delete(productId, auctionId, user);
		
		return SuccessApiResponse.<String>builder()
				.status(200)
				.data("successfully deleted")
				.build();
	}
	
	@GetMapping(path = "/winner", produces = MediaType.APPLICATION_JSON_VALUE)
	public SuccessApiResponse<AuctionResponse> getWinner(@PathVariable("productId") Long productId) {
		AuctionResponse response = auctionService.winnerAuction(productId);
		
		return SuccessApiResponse.<AuctionResponse>builder()
				.status(200)
				.data(response)
				.build();
	}
	
}
