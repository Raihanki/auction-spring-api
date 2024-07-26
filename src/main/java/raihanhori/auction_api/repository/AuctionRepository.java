package raihanhori.auction_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import raihanhori.auction_api.entity.Auction;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

	@Lock(LockModeType.PESSIMISTIC_READ)
	@Query("SELECT a FROM Auction a WHERE a.product.id = :productId")
	Page<Auction> findFirstByProductIdOrderByIdDescForRead(@Param("productId") Long productId, Pageable pageable);
	
	@Query("SELECT a FROM Auction a JOIN Product p ON a.product.id = p.id WHERE a.user.id = :userId ORDER BY a.createdAt DESC")
	Page<Auction> findAuctionByUserId(@Param("userId") Long userId, Pageable pageable);
	
	@Query("SELECT a FROM Auction a WHERE a.product.id = :productId ORDER BY a.createdAt DESC")
	Page<Auction> findWinner(Long productId, Pageable pageable);
	
	@Query("SELECT a FROM Auction a JOIN User u ON a.user.id = u.id WHERE a.product.id = :productId ORDER BY a.createdAt DESC")
	Page<Auction> findAllByProductId(@Param("productId") Long productId, Pageable pageable);

}
