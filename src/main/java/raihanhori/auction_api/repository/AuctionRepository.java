package raihanhori.auction_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import raihanhori.auction_api.entity.Auction;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

}
