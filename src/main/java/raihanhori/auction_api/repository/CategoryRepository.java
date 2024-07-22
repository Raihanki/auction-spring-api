package raihanhori.auction_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import raihanhori.auction_api.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	Optional<Category> findFirstByName(String name);
	
	@Query("SELECT c FROM Category c WHERE c.name = :name and c.id != :id")
	Optional<Category> findFirstByNameExceptId(@Param("name") String name, @Param("id") Long id);
	
}
