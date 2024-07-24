package raihanhori.auction_api.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import raihanhori.auction_api.entity.Auction;
import raihanhori.auction_api.entity.Product;
import raihanhori.auction_api.entity.User;
import raihanhori.auction_api.repository.AuctionRepository;
import raihanhori.auction_api.repository.ProductRepository;
import raihanhori.auction_api.request.auction.CreateAuctionRequest;
import raihanhori.auction_api.request.auction.GetAuctionRequest;
import raihanhori.auction_api.response.AuctionResponse;
import raihanhori.auction_api.response.MyAuctionResponse;
import raihanhori.auction_api.response.UserResponse;

@Service
public class AuctionServiceImpl implements AuctionService {

	@Autowired
	private AuctionRepository auctionRepository;

	@Autowired
	private ProductRepository productRepository;

	@Override
	public Page<AuctionResponse> getAll(GetAuctionRequest request, Long productId, User user) {
		this.getProductById(productId);

		Pageable pageable = PageRequest.of(request.getPage() - 1, request.getLimit(),
				Sort.by("createdAt").descending());

		Page<Auction> auctions = auctionRepository.findAllByProductId(productId, pageable);

		List<AuctionResponse> listAuction = auctions.stream().map(auction -> toAuctionResponse(auction)).toList();

		return new PageImpl<AuctionResponse>(listAuction, pageable, auctions.getTotalElements());
	}

	@Transactional
	@Override
	public void create(CreateAuctionRequest request, User user) {
		Product product = this.getProductById(request.getProductId());

		if (product.getWinnerUser() != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "auction is already have a winner");
		}

		Auction lastAuction = auctionRepository.findFirstByProductIdOrderByIdDescForRead(product.getId()).orElse(null);
		if (lastAuction != null && lastAuction.getPrice().compareTo(request.getPrice()) >= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you need to put higher price");
		} else {
			if (product.getStartPrice().compareTo(request.getPrice()) >= 0) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you need to put higher price");
			}
			
			BigDecimal priceMultiples = product.getPriceMultiples();
			BigDecimal priceDifference = request.getPrice().subtract(product.getStartPrice());

			if (priceDifference.remainder(priceMultiples).compareTo(BigDecimal.ZERO) != 0) {
			    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "price must be in multiples of " + priceMultiples);
			}
		}

		Auction auction = new Auction();
		auction.setProduct(product);
		auction.setUser(user);
		auction.setPrice(request.getPrice());
		auctionRepository.save(auction);
	}

	@Override
	public void delete(Long productId, Long auctionId, User user) {
		this.getProductById(productId);

		Auction auction = auctionRepository.findById(auctionId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "auction not found"));

		auctionRepository.delete(auction);
	}

	@Override
	public Page<MyAuctionResponse> getMyAuction(GetAuctionRequest request, User user) {
		Pageable pageable = PageRequest.of(request.getPage() + 1, request.getLimit(),
				Sort.by("createdAt").descending());

		Page<Auction> auctions = auctionRepository.findAuctionByUserId(user.getId(), pageable);

		List<MyAuctionResponse> listAuction = auctions.stream()
				.map(auction -> toMyAuctionResponse(auction, user.getId())).toList();

		return new PageImpl<MyAuctionResponse>(listAuction, pageable, auctions.getTotalElements());
	}
	
	@Override
	public AuctionResponse winnerAuction(Long productId) {
		this.getProductById(productId);
		
		Pageable pageable = PageRequest.of(0, 1, Sort.by("id").descending());
		
		List<Auction> auctions = auctionRepository.findWinner(productId, pageable);
		Auction auction = auctions.get(0);
		
		return toAuctionResponse(auction);
	}

	private Product getProductById(Long productId) {
		return productRepository.findByIdWithoutRelation(productId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product not found"));
	}

	private AuctionResponse toAuctionResponse(Auction auction) {
		UserResponse user = UserResponse.builder().id(auction.getUser().getId()).name(auction.getUser().getName())
				.email(auction.getUser().getEmail()).createdAt(auction.getUser().getCreatedAt()).build();

		return AuctionResponse.builder().user(user).productId(auction.getProduct().getId()).price(auction.getPrice())
				.createdAt(auction.getCreatedAt()).build();
	}
	
	private MyAuctionResponse toMyAuctionResponse(Auction auction, Long userId) {

		return MyAuctionResponse.builder().userId(userId)
				.productId(auction.getProduct().getId())
				.productName(auction.getProduct().getName())
				.price(auction.getPrice())
				.createdAt(auction.getCreatedAt())
				.build();
	}

}
