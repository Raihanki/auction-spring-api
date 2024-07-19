package raihanhori.auction_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import raihanhori.auction_api.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
