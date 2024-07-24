package raihanhori.auction_api;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import raihanhori.auction_api.entity.Auction;
import raihanhori.auction_api.entity.Category;
import raihanhori.auction_api.entity.Currency;
import raihanhori.auction_api.entity.Product;
import raihanhori.auction_api.entity.Role;
import raihanhori.auction_api.entity.User;
import raihanhori.auction_api.helper.DataPaginationResponse;
import raihanhori.auction_api.helper.ErrorApiResponse;
import raihanhori.auction_api.helper.SuccessApiResponse;
import raihanhori.auction_api.repository.AuctionRepository;
import raihanhori.auction_api.repository.CategoryRepository;
import raihanhori.auction_api.repository.ProductRepository;
import raihanhori.auction_api.repository.UserRepository;
import raihanhori.auction_api.request.auction.CreateAuctionRequest;
import raihanhori.auction_api.response.AuctionResponse;
import raihanhori.auction_api.response.MyAuctionResponse;
import raihanhori.auction_api.security.JwtUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
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
public class AuctionControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private AuctionRepository auctionRepository;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private JwtUtils jwtUtils;
	
	private User user;
	
	private String token;
	
	private Category category;
	
	@BeforeEach
	void setUp() {
		auctionRepository.deleteAll();
		productRepository.deleteAll();
		categoryRepository.deleteAll();
		userRepository.deleteAll();
		
		user = new User();
		user.setEmail("test@example.com");
		user.setPassword(passwordEncoder.encode("password"));
		user.setName("test");
		user.setRole(Role.USER);
		userRepository.save(user);
		
		token = jwtUtils.generateToken(user);
		
		category = categoryRepository.save(Category.builder().name("test").build());
	}
	
	@AfterEach
	void tearDown() {
		auctionRepository.deleteAll();
		productRepository.deleteAll();
		categoryRepository.deleteAll();
		userRepository.deleteAll();
	}
	
	@Test
	void testCreateSuccess() throws Exception {
		Product product = new Product();
		product.setCategory(category);
		product.setOwner(user);
		product.setName("test");
		product.setDescription("test");
		product.setStartPrice(new BigDecimal("100000.00"));
		product.setPriceMultiples(new BigDecimal("10000.00"));
		product.setCurrency(Currency.IDR);
		product.setEndAuctionDate(new Timestamp(Date.from(LocalDate.of(2024, 7, 25).atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime()));
		productRepository.save(product);
		
		CreateAuctionRequest request = new CreateAuctionRequest();
		request.setPrice(new BigDecimal("120000.00"));
		
		mockMvc.perform(
				post("/api/v1/products/" + product.getId() + "/auctions")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isCreated())
		.andDo(result -> {
			SuccessApiResponse<String> response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
		
			assertNotNull(response.getData());
			
			Pageable pageable = PageRequest.of(0, 1);
			
			Page<Auction> productAuctions = auctionRepository.findAllByProductId(product.getId(), pageable);
			Auction auction = productAuctions.getContent().get(0);
			
			assertEquals(user.getName(), auction.getUser().getName());
			assertEquals(new BigDecimal("120000.00"), auction.getPrice());
		});
		
	}
	
	@Test
	void testCreateFailedPriceLow() throws Exception {
		Product product = new Product();
		product.setCategory(category);
		product.setOwner(user);
		product.setName("test");
		product.setDescription("test");
		product.setStartPrice(new BigDecimal("100000.00"));
		product.setPriceMultiples(new BigDecimal("10000.00"));
		product.setCurrency(Currency.IDR);
		product.setEndAuctionDate(new Timestamp(Date.from(LocalDate.of(2024, 7, 25).atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime()));
		productRepository.save(product);
		
		CreateAuctionRequest request = new CreateAuctionRequest();
		request.setPrice(new BigDecimal("100000.00"));
		
		mockMvc.perform(
				post("/api/v1/products/" + product.getId() + "/auctions")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isBadRequest())	
		.andDo(result -> {
			ErrorApiResponse response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
		
			assertNotNull(response.getMessage());
		});
		
	}
	
	@Test
	void testGetAll() throws Exception {
		Product product = new Product();
		product.setCategory(category);
		product.setOwner(user);
		product.setName("test");
		product.setDescription("test");
		product.setStartPrice(new BigDecimal("100000.00"));
		product.setPriceMultiples(new BigDecimal("10000.00"));
		product.setCurrency(Currency.IDR);
		product.setEndAuctionDate(new Timestamp(Date.from(LocalDate.of(2024, 7, 25).atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime()));
		productRepository.save(product);
		
		Auction auction = new Auction();
		auction.setUser(user);
		auction.setProduct(product);
		auction.setPrice(new BigDecimal("200000.00"));
		auctionRepository.save(auction);
		
		Auction auction2 = new Auction();
		auction2.setUser(user);
		auction2.setProduct(product);
		auction2.setPrice(new BigDecimal("240000.00"));
		auctionRepository.save(auction2);
		
		mockMvc.perform(
				get("/api/v1/products/" + product.getId() + "/auctions")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token)
		).andExpect(status().isOk())	
		.andDo(result -> {
			DataPaginationResponse<List<AuctionResponse>> response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
		
			assertNotNull(response.getData());
			
			assertEquals(2, response.getMeta().getTotal_item());
		});
		
	}
	
	@Test
	void testMyAuction() throws Exception {
		Product product = new Product();
		product.setCategory(category);
		product.setOwner(user);
		product.setName("test");
		product.setDescription("test");
		product.setStartPrice(new BigDecimal("100000.00"));
		product.setPriceMultiples(new BigDecimal("10000.00"));
		product.setCurrency(Currency.IDR);
		product.setEndAuctionDate(new Timestamp(Date.from(LocalDate.of(2024, 7, 25).atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime()));
		productRepository.save(product);
		
		Auction auction = new Auction();
		auction.setUser(user);
		auction.setProduct(product);
		auction.setPrice(new BigDecimal("200000.00"));
		auctionRepository.save(auction);
		
		Auction auction2 = new Auction();
		auction2.setUser(user);
		auction2.setProduct(product);
		auction2.setPrice(new BigDecimal("240000.00"));
		auctionRepository.save(auction2);
		
		User user2 = new User();
		user2.setEmail("test2@example.com");
		user2.setPassword(passwordEncoder.encode("password"));
		user2.setName("test2");
		user2.setRole(Role.USER);
		userRepository.save(user2);
		
		Auction auction3 = new Auction();
		auction3.setUser(user2);
		auction3.setProduct(product);
		auction3.setPrice(new BigDecimal("260000.00"));
		auctionRepository.save(auction3);
		
		mockMvc.perform(
				get("/api/v1/products/" + product.getId() + "/auctions/my")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token)
		).andExpect(status().isOk())	
		.andDo(result -> {
			DataPaginationResponse<List<MyAuctionResponse>> response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
		
			assertNotNull(response.getData());
			
			assertEquals(2, response.getMeta().getTotal_item());
			response.getData().stream().forEach(res -> {
				assertEquals(user.getId(), res.getUserId());
			});
		});
		
	}
	
	@Test
	void testGetWinner() throws Exception {
		Product product = new Product();
		product.setCategory(category);
		product.setOwner(user);
		product.setName("test");
		product.setDescription("test");
		product.setStartPrice(new BigDecimal("100000.00"));
		product.setPriceMultiples(new BigDecimal("10000.00"));
		product.setCurrency(Currency.IDR);
		product.setEndAuctionDate(new Timestamp(Date.from(LocalDate.of(2024, 7, 25).atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime()));
		productRepository.save(product);
		
		Auction auction = new Auction();
		auction.setUser(user);
		auction.setProduct(product);
		auction.setPrice(new BigDecimal("200000.00"));
		auctionRepository.save(auction);
		
		Auction auction2 = new Auction();
		auction2.setUser(user);
		auction2.setProduct(product);
		auction2.setPrice(new BigDecimal("240000.00"));
		auctionRepository.save(auction2);
		
		User user2 = new User();
		user2.setEmail("test2@example.com");
		user2.setPassword(passwordEncoder.encode("password"));
		user2.setName("test2");
		user2.setRole(Role.USER);
		userRepository.save(user2);
		
		Auction auction3 = new Auction();
		auction3.setUser(user2);
		auction3.setProduct(product);
		auction3.setPrice(new BigDecimal("260000.00"));
		auctionRepository.save(auction3);
		
		product.setWinnerUser(user2);
		
		mockMvc.perform(
				get("/api/v1/products/" + product.getId() + "/auctions/winner")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token)
		).andExpect(status().isOk())	
		.andDo(result -> {
			SuccessApiResponse<AuctionResponse> response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
		
			assertNotNull(response.getData());
			
			assertEquals(user2.getId(), response.getData().getUser().getId());
		});
	}
	
}
