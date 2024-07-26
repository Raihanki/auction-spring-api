package raihanhori.auction_api;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import raihanhori.auction_api.entity.Auction;
import raihanhori.auction_api.entity.Category;
import raihanhori.auction_api.entity.Currency;
import raihanhori.auction_api.entity.Image;
import raihanhori.auction_api.entity.Product;
import raihanhori.auction_api.entity.Role;
import raihanhori.auction_api.entity.User;
import raihanhori.auction_api.helper.DataPaginationResponse;
import raihanhori.auction_api.helper.ErrorApiResponse;
import raihanhori.auction_api.helper.SuccessApiResponse;
import raihanhori.auction_api.repository.AuctionRepository;
import raihanhori.auction_api.repository.CategoryRepository;
import raihanhori.auction_api.repository.ImageRepository;
import raihanhori.auction_api.repository.ProductRepository;
import raihanhori.auction_api.repository.UserRepository;
import raihanhori.auction_api.response.ProductResponse;
import raihanhori.auction_api.security.JwtUtils;
import raihanhori.auction_api.service.ImageService;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ImageRepository imageRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private ImageService imageService;
	
	@Autowired
	private AuctionRepository auctionRepository;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private JwtUtils jwtUtils;
	
//	@Autowired
//	private ChooseAuctionWinnerJob chooseAuctionWinnerJob;
	
//	@Autowired
//	Scheduler scheduler;
	
//	@MockBean
//	private ProductServiceImpl productServiceImpl;
	
	private User user;
	
	private User owner;
	
	private String token;
	
	private String tokenOwner;
	
	private Category category;
	
	@BeforeEach
	void setUp() {
		auctionRepository.deleteAll();
		imageRepository.deleteAll();
		productRepository.deleteAll();
		categoryRepository.deleteAll();
		userRepository.deleteAll();
		
		user = new User();
		user.setEmail("test@example.com");
		user.setPassword(passwordEncoder.encode("password"));
		user.setName("test");
		user.setRole(Role.ADMIN);
		userRepository.save(user);
		
		owner = new User();
		owner.setEmail("own@example.com");
		owner.setPassword(passwordEncoder.encode("password"));
		owner.setName("owner");
		owner.setRole(Role.OWNER);
		userRepository.save(owner);
		
		token = jwtUtils.generateToken(user);
		tokenOwner = jwtUtils.generateToken(owner);
		
		category = categoryRepository.save(Category.builder().name("test").build());
	}
	
	@AfterEach
	void tearDown() {
		auctionRepository.deleteAll();
		imageRepository.deleteAll();
		productRepository.deleteAll();
		categoryRepository.deleteAll();
		userRepository.deleteAll();
	}
	
	@Test
	void testCreateSuccess() throws Exception {
//		Scheduler mockScheduler = mock(Scheduler.class);
//		
//		ProductServiceImpl service = spy(new ProductServiceImpl());
//	    service.setScheduler(mockScheduler);
//	    
//	    doReturn(mockScheduler).when(service).getScheduler();
		
		Path path1 = Paths.get("/Users/raihanhori/dev/java/tes-upload1.png");
		Path path2 = Paths.get("/Users/raihanhori/dev/java/tes-upload2.png");
		
		MockMultipartFile file1 = new MockMultipartFile("images", "tes-upload1.png", MediaType.IMAGE_PNG_VALUE, Files.readAllBytes(path1));
		MockMultipartFile file2 = new MockMultipartFile("images", "tes-upload2.png", MediaType.IMAGE_PNG_VALUE, Files.readAllBytes(path2));
		
		mockMvc.perform(
				multipart("/api/v1/products")
				.file(file1)
				.file(file2)
				.param("categoryId", category.getId().toString())
	            .param("name", "test")
	            .param("description", "test")
	            .param("startPrice", "100000")
	            .param("priceMultiples", "20000")
	            .param("currency", "IDR")
	            .param("endAuctionDate", "2024-07-27 00:00:00")
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + tokenOwner)
		).andExpect(status().isCreated())
		.andDo(result -> {
			SuccessApiResponse<String> response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			assertNotNull(response.getData());
			
			Product createdProduct = productRepository.findFirstByName("test").orElse(null);
			assertNotNull(createdProduct);
			
			assertEquals(category.getId(), createdProduct.getCategory().getId());
			assertEquals("test", createdProduct.getName());
			assertEquals("test", createdProduct.getDescription());
			assertEquals(new BigDecimal("100000.00"), createdProduct.getStartPrice());
			assertEquals(new BigDecimal("20000.00"), createdProduct.getPriceMultiples());
			assertEquals(Currency.IDR, createdProduct.getCurrency());
			assertEquals(Date.from(LocalDate.of(2024, 7, 27).atStartOfDay(ZoneId.systemDefault()).toInstant()), createdProduct.getEndAuctionDate());
			
			 List<Image> images = imageRepository.findAllByProduct_Id(createdProduct.getId());
			 assertEquals(2, images.size());
			 
			 images.forEach(image -> imageService.delete(image.getImageUrl()));
			 
			 Auction auction = new Auction();
			 auction.setUser(user);
			 auction.setProduct(createdProduct);
			 auction.setPrice(new BigDecimal("200000.00"));
			 auctionRepository.save(auction);
			 
//			 // Verify that ProductServiceImpl.create is called correctly
//			 verify(productServiceImpl, times(1)).create(any(CreateProductRequest.class));
//
//			 // Verify that scheduler.scheduleJob is called
//			 verify(mockScheduler, times(1)).scheduleJob(any(JobDetail.class), any(Trigger.class));
		});
		
//		//Manually execute the job to verify its functionality
//		Product product = productRepository.findFirstByName("test").orElse(null);
//        JobDetail jobDetail = JobBuilder.newJob(ChooseAuctionWinnerJob.class)
//                .withIdentity("auctionEndJob_1", "auctionJobs")
//                .usingJobData("productId", product.getId())
//                .build();
//
//        Trigger trigger = TriggerBuilder.newTrigger()
//                .withIdentity("auctionEndTrigger_1", "auctionTriggers")
//                .startNow()
//                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
//                .build();
//   
//        // Schedule and execute the job manually
//        scheduler.scheduleJob(jobDetail, trigger);
//        
//        Thread.sleep(3000L);
//        // Verify the effect of job execution
//        assertNotNull(product.getWinnerUser());
	}
	
	@Test
	void testCreateFailedValidation() throws Exception {
		mockMvc.perform(
				multipart("/api/v1/products")
				.param("categoryId", category.getId().toString())
	            .param("name", "")
	            .param("description", "")
	            .param("startPrice", "")
	            .param("priceMultiples", "")
	            .param("currency", "IDR")
	            .param("endAuctionDate", "2024-07-24 00:00:00")
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + tokenOwner)
		).andExpect(status().isBadRequest())
		.andDo(result -> {
			ErrorApiResponse response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			assertNotNull(response.getMessage());
			
		});
	}
	
	@Test
	void testUpdateSuccess() throws Exception {	
		
		Path path1 = Paths.get("/Users/raihanhori/dev/java/tes-upload1.png");
		Path path2 = Paths.get("/Users/raihanhori/dev/java/tes-upload2.png");
		
		MockMultipartFile file1 = new MockMultipartFile("images", "tes-upload1.png", MediaType.IMAGE_PNG_VALUE, Files.readAllBytes(path1));
		MockMultipartFile file2 = new MockMultipartFile("images", "tes-upload2.png", MediaType.IMAGE_PNG_VALUE, Files.readAllBytes(path2));
		
		String savedPath = imageService.savePublic(file1);
		
		Product product = new Product();
		product.setCategory(category);
		product.setOwner(owner);
		product.setName("test");
		product.setDescription("test");
		product.setStartPrice(new BigDecimal("100000.00"));
		product.setPriceMultiples(new BigDecimal("10000.00"));
		product.setCurrency(Currency.IDR);
		product.setEndAuctionDate(new Timestamp(Date.from(LocalDate.of(2024, 7, 27).atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime()));
		productRepository.save(product);
		
		imageRepository.save(Image.builder().product(product).imageUrl(savedPath).build());
		
		Category category2 = categoryRepository.save(Category.builder().name("test-2").build());
		
		mockMvc.perform(
				multipart("/api/v1/products/" + product.getId())
				.file(file1)
				.file(file2)
				.param("categoryId", category2.getId().toString())
	            .param("name", "test-edited")
	            .param("description", "test-edited")
	            .param("startPrice", "100")
	            .param("priceMultiples", "10")
	            .param("currency", "USD")
	            .param("endAuctionDate", "2024-07-28 00:00:00")
	            .param("winnerUserId", user.getId().toString())
	            .param("endPrice", "300")
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + tokenOwner)
		).andExpect(status().isOk())
		.andDo(result -> {
			SuccessApiResponse<String> response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			assertNotNull(response.getData());
			
			Product createdProduct = productRepository.findFirstByName("test").orElse(null);
			assertNull(createdProduct);
			
			Product updatedProduct = productRepository.findFirstByName("test-edited").orElse(null);
			assertNotNull(updatedProduct);
			
			assertEquals(category2.getId(), updatedProduct.getCategory().getId());
			assertEquals("test-edited", updatedProduct.getName());
			assertEquals("test-edited", updatedProduct.getDescription());
			assertEquals(new BigDecimal("100.00"), updatedProduct.getStartPrice());
			assertEquals(new BigDecimal("10.00"), updatedProduct.getPriceMultiples());
			assertEquals(Currency.USD, updatedProduct.getCurrency());
			assertEquals(user.getId(), updatedProduct.getWinnerUser().getId());
			assertEquals(new BigDecimal("300.00"), updatedProduct.getEndPrice());
			assertEquals(Date.from(LocalDate.of(2024, 7, 28).atStartOfDay(ZoneId.systemDefault()).toInstant()), updatedProduct.getEndAuctionDate());
			
			imageService.getImagesByProductId(updatedProduct.getId())
				.forEach(image -> imageService.delete(image.getImageUrl()));
		});
	}
	
	@Test
	void deleteProduct() throws Exception {
		Path path1 = Paths.get("/Users/raihanhori/dev/java/tes-upload1.png");
		
		MockMultipartFile file1 = new MockMultipartFile("images", "tes-upload1.png", MediaType.IMAGE_PNG_VALUE, Files.readAllBytes(path1));
		
		String savedPath = imageService.savePublic(file1);
		
		Product product = new Product();
		product.setCategory(category);
		product.setOwner(owner);
		product.setName("test");
		product.setDescription("test");
		product.setStartPrice(new BigDecimal("100000.00"));
		product.setPriceMultiples(new BigDecimal("10000.00"));
		product.setCurrency(Currency.IDR);
		product.setEndAuctionDate(new Timestamp(Date.from(LocalDate.of(2024, 7, 25).atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime()));
		productRepository.save(product);
		
		imageRepository.save(Image.builder().product(product).imageUrl(savedPath).build());
		
		mockMvc.perform(
				delete("/api/v1/products/" + product.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + tokenOwner)
		).andExpect(status().isOk())
		.andDo(result -> {
			SuccessApiResponse<String> response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			assertNotNull(response.getData());
			
			Product deletedProduct = productRepository.findById(product.getId()).orElse(null);
			assertNull(deletedProduct);
			
			assertEquals(0, imageRepository.findAllByProduct_Id(product.getId()).size());
		});
	}
	
	@Test
	void detailProduct() throws Exception {
		Path path1 = Paths.get("/Users/raihanhori/dev/java/tes-upload1.png");
		
		MockMultipartFile file1 = new MockMultipartFile("images", "tes-upload1.png", MediaType.IMAGE_PNG_VALUE, Files.readAllBytes(path1));
		
		String savedPath = imageService.savePublic(file1);
		
		Product product = new Product();
		product.setCategory(category);
		product.setOwner(user);
		product.setName("test");
		product.setDescription("test");
		product.setStartPrice(new BigDecimal("100000.00"));
		product.setPriceMultiples(new BigDecimal("10000.00"));
		product.setCurrency(Currency.IDR);
		product.setEndAuctionDate(new Timestamp(Date.from(LocalDate.of(2024, 7, 24).atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime()));
		productRepository.save(product);
		
		imageRepository.save(Image.builder().product(product).imageUrl(savedPath).build());
		
		mockMvc.perform(
				get("/api/v1/products/" + product.getId())
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token)
		).andExpect(status().isOk())
		.andDo(result -> {
			SuccessApiResponse<ProductResponse> response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			assertNotNull(response.getData());
			
			Product createdProduct = productRepository.findById(product.getId()).orElse(null);
			assertNotNull(createdProduct);
			
			assertEquals(category.getId(), response.getData().getCategory().getId());
			assertEquals("test", response.getData().getName());
			assertEquals("test", response.getData().getDescription());
			assertEquals(new BigDecimal("100000.00"), response.getData().getStartPrice());
			assertEquals(new BigDecimal("10000.00"), response.getData().getPriceMultiples());
			assertEquals(Currency.IDR, response.getData().getCurrency());
			assertEquals(Date.from(LocalDate.of(2024, 7, 24).atStartOfDay(ZoneId.systemDefault()).toInstant()), response.getData().getEndAuctionDate());
			assertEquals(1, imageRepository.findAllByProduct_Id(response.getData().getId()).size());
			
			imageService.getImagesByProductId(response.getData().getId())
				.forEach(image -> imageService.delete(image.getImageUrl()));
		});
	}
	
	@Test
	void detailProductNotFound() throws Exception {
		mockMvc.perform(
				get("/api/v1/products/121212121")
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token)
		).andExpect(status().isNotFound())
		.andDo(result -> {
			ErrorApiResponse response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			assertNotNull(response.getMessage());
		});
	}
	
	@Test
	void getAllProduct() throws Exception {
		Path path1 = Paths.get("/Users/raihanhori/dev/java/tes-upload1.png");
		
		MockMultipartFile file1 = new MockMultipartFile("images", "tes-upload1.png", MediaType.IMAGE_PNG_VALUE, Files.readAllBytes(path1));
		
		String savedPath = imageService.savePublic(file1);
		
		Product product = new Product();
		product.setCategory(category);
		product.setOwner(user);
		product.setName("test");
		product.setDescription("test");
		product.setStartPrice(new BigDecimal("100000.00"));
		product.setPriceMultiples(new BigDecimal("10000.00"));
		product.setCurrency(Currency.IDR);
		product.setEndAuctionDate(new Timestamp(Date.from(LocalDate.of(2024, 7, 24).atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime()));
		productRepository.save(product);
		
		imageRepository.save(Image.builder().product(product).imageUrl(savedPath).build());
		
		mockMvc.perform(
				get("/api/v1/products")
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token)
		).andExpect(status().isOk())
		.andDo(result -> {
			DataPaginationResponse<List<ProductResponse>> response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			assertEquals(1, response.getData().size());
			
			imageService.getImagesByProductId(product.getId())
				.forEach(image -> imageService.delete(image.getImageUrl()));
		});
	}
	
}
