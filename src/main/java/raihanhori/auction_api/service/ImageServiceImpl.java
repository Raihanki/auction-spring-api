package raihanhori.auction_api.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import raihanhori.auction_api.entity.Image;
import raihanhori.auction_api.repository.ImageRepository;

@Service
public class ImageServiceImpl implements ImageService {
	
	@Autowired
	private ImageRepository imageRepository;

	private final Path root = Paths.get("upload/products/");
	private final Path folderPrivate = Paths.get("upload/products/private");
	private final Path folderPublic = Paths.get("upload/products/public");

	@Override
	public void init() {
		try {
			if (!Files.exists(root)) {
                Files.createDirectories(root);
            }
            if (!Files.exists(folderPrivate)) {
                Files.createDirectories(folderPrivate);
            }
            if (!Files.exists(folderPublic)) {
                Files.createDirectories(folderPublic);
            }
		} catch (IOException e) {
			throw new RuntimeException("Could not initialize folder for upload! " + e.getMessage());
		}
	}

	@Override
	public String savePublic(MultipartFile file) {
		try {
			String filePath = System.currentTimeMillis() + "-" + file.getOriginalFilename();
			Files.copy(file.getInputStream(), this.folderPublic.resolve(filePath));
			
			return filePath;
		} catch (Exception e) {
			throw new RuntimeException("Could not store the public file, with error : " + e.getMessage());
		}
	}

	@Override
	public void savePrivate(MultipartFile file) {
		try {
			Files.copy(file.getInputStream(),
					this.folderPrivate.resolve(file.getOriginalFilename() + "-" + System.currentTimeMillis()));
		} catch (Exception e) {
			throw new RuntimeException("Could not store the private file, with error : " + e.getMessage());
		}
	}

	@Override
	public Resource load(String filename) {
		try {
			Path file = folderPublic.resolve(filename);
			Resource resource = new UrlResource(file.toUri());

			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new RuntimeException("Could not read the file!");
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error load file : " + e);
		}
	}

	@Override
	public void delete(String filename) {
		Path file = folderPublic.resolve(filename);
		try {
			boolean deleteFile = Files.deleteIfExists(file);
			if (deleteFile == false) {
				throw new RuntimeException("Could not delete the file!");
			}
		} catch (IOException e) {
			throw new RuntimeException("Error while delting file!");
		}
		
	}

	@Override
	public void saveDatabase(Image image) {
		imageRepository.save(image);
	}

	@Override
	public List<Image> getImagesByProductId(Long productId) {
		return imageRepository.findAllByProduct_Id(productId);
	}

	@Override
	public void deleteDatabase(Image image) {
		imageRepository.delete(image);
	}

}
