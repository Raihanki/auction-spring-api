package raihanhori.auction_api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import raihanhori.auction_api.service.ImageService;

@SpringBootApplication
@EnableJpaAuditing
public class AuctionApiApplication implements CommandLineRunner{
	
	@Autowired
	private ImageService imageService;

	public static void main(String[] args) {
		SpringApplication.run(AuctionApiApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		imageService.init();
	}

}
