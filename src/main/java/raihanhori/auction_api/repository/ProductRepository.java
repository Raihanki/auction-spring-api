package raihanhori.auction_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import raihanhori.auction_api.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
