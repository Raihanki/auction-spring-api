package raihanhori.auction_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import raihanhori.auction_api.entity.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

	List<Image> findAllByProduct_Id(Long id);
	
}
