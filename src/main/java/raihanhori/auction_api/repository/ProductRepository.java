package raihanhori.auction_api.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import raihanhori.auction_api.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	@Query("SELECT p FROM Product p " +
	           "JOIN FETCH p.category c " +
	           "JOIN FETCH p.owner o " +
	           "LEFT JOIN FETCH p.winnerUser w " +
	           "WHERE (:name IS NULL OR p.name LIKE %:name%)")
	Page<Product> getAndSearchProduct(@Param("name") String name, Pageable pageable);
	
	@Query("SELECT p FROM Product p " +
	           "JOIN FETCH p.category c " +
	           "JOIN FETCH p.owner o " +
	           "LEFT JOIN FETCH p.winnerUser w " +
	           "WHERE (:name IS NULL OR p.name LIKE %:name%)" +
	           "AND o.id = :userId")
	Page<Product> getByUserIdAndSearchProduct(@Param("name") String name, @Param("userId") Long userId, Pageable pageable);
	
	Optional<Product> findFirstByName(String name);
	
	@Query("SELECT p FROM Product p JOIN User u ON p.owner.id = u.id WHERE p.id = :productId")
	Optional<Product> findByIdWithoutRelation(@Param("productId") Long productId);
	
}
