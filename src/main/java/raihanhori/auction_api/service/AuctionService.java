package raihanhori.auction_api.service;

import org.springframework.data.domain.Page;

import raihanhori.auction_api.entity.User;
import raihanhori.auction_api.request.auction.CreateAuctionRequest;
import raihanhori.auction_api.request.auction.GetAuctionRequest;
import raihanhori.auction_api.response.AuctionResponse;
import raihanhori.auction_api.response.MyAuctionResponse;

public interface AuctionService {

	Page<AuctionResponse> getAll(GetAuctionRequest request, Long productId, User user);
	
	void create(CreateAuctionRequest request, User user);
	
	void delete(Long productId, Long auctionId, User user);
	
	Page<MyAuctionResponse> getMyAuction(GetAuctionRequest request, User user);
	
	AuctionResponse winnerAuction(Long productId);
	
}
