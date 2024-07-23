package raihanhori.auction_api.service;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import raihanhori.auction_api.entity.Image;

public interface ImageService {
	
	void init();
	
	String savePublic(MultipartFile file);
	
	void savePrivate(MultipartFile file);
	
	Resource load(String filename);
	
	void delete(String filename);
	
	void saveDatabase(Image image);
	
	void deleteDatabase(Image image);
	
	List<Image> getImagesByProductId(Long productId);
	
}
