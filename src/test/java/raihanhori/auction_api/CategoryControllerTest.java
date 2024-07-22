package raihanhori.auction_api;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import raihanhori.auction_api.entity.Category;
import raihanhori.auction_api.entity.Role;
import raihanhori.auction_api.entity.User;
import raihanhori.auction_api.helper.ErrorApiResponse;
import raihanhori.auction_api.helper.SuccessApiResponse;
import raihanhori.auction_api.repository.CategoryRepository;
import raihanhori.auction_api.repository.UserRepository;
import raihanhori.auction_api.request.category.CreateCategoryRequest;
import raihanhori.auction_api.request.category.UpdateCategoryRequest;
import raihanhori.auction_api.response.CategoryResponse;
import raihanhori.auction_api.security.JwtUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private JwtUtils jwtUtils;
	
	private User user;
	
	private String token;
	
	@BeforeEach
	void setUp() {
		user = new User();
		user.setEmail("test@example.com");
		user.setPassword(passwordEncoder.encode("password"));
		user.setName("test");
		user.setRole(Role.ADMIN);
		userRepository.save(user);
		
		token = jwtUtils.generateToken(user);
	}
	
	@AfterEach
	void tearDown() {
		userRepository.deleteAll();
		categoryRepository.deleteAll();
	}
	
	@Test
	void testCreateSuccess() throws Exception {
		CreateCategoryRequest request = new CreateCategoryRequest();
		request.setName("sport");
		
		mockMvc.perform(
				post("/api/v1/categories")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header("Authorization", "Bearer " + token)
		).andExpect(status().isCreated())
		.andDo(result -> {
			SuccessApiResponse<String> response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			
			assertNotNull(response.getData());
			
			Category category = categoryRepository.findFirstByName(request.getName()).orElse(null);
			assertNotNull(category);
			assertEquals(request.getName(), category.getName());
		});
	}
	
	@Test
	void testCreateFailedValidation() throws Exception {
		CreateCategoryRequest request = new CreateCategoryRequest();
		request.setName("");
		
		mockMvc.perform(
				post("/api/v1/categories")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header("Authorization", "Bearer " + token)
		).andExpect(status().isBadRequest())
		.andDo(result -> {
			ErrorApiResponse response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			
			assertNotNull(response.getMessage());
		});
	}
	
	@Test
	void testCreateFailedAlreadyExist() throws Exception {
		categoryRepository.save(Category.builder().name("test").build());
		
		CreateCategoryRequest request = new CreateCategoryRequest();
		request.setName("test");
		
		mockMvc.perform(
				post("/api/v1/categories")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header("Authorization", "Bearer " + token)
		).andExpect(status().isBadRequest())
		.andDo(result -> {
			ErrorApiResponse response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			
			assertNotNull(response.getMessage());
		});
	}
	
	@Test
	void testGetAll() throws Exception {
		categoryRepository.save(Category.builder().name("test1").build());
		categoryRepository.save(Category.builder().name("test2").build());
		
		mockMvc.perform(
				get("/api/v1/categories")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token)
		).andExpect(status().isOk())
		.andDo(result -> {
			SuccessApiResponse<List<CategoryResponse>> response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			
			assertNotNull(response.getData());
			assertEquals(2, response.getData().size());
			
			Category category1 = categoryRepository.findFirstByName("test1").orElse(null);
			assertNotNull(category1);
			Category category2 = categoryRepository.findFirstByName("test2").orElse(null);
			assertNotNull(category2);
		});
	}
	
	@Test
	void testUpdateSuccess() throws Exception {
		Category category = new Category();
		category.setName("test");
		categoryRepository.save(category);
		
		UpdateCategoryRequest request = new UpdateCategoryRequest();
		request.setName("test-updated");
		
		mockMvc.perform(
				patch("/api/v1/categories/" + (Long) category.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header("Authorization", "Bearer " + token)
		).andExpect(status().isOk())
		.andDo(result -> {
			SuccessApiResponse<String> response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			
			assertNotNull(response.getData());
			
			Category checkUpdatedCategory = categoryRepository.findFirstByName(request.getName()).orElse(null);
			assertNotNull(checkUpdatedCategory);
			
			Category checkOldCategory = categoryRepository.findFirstByName(category.getName()).orElse(null);
			assertNull(checkOldCategory);
		});
	}
	
	@Test
	void testUpdateFailedValidation() throws Exception {
		Category category = new Category();
		category.setName("test");
		categoryRepository.save(category);
		
		UpdateCategoryRequest request = new UpdateCategoryRequest();
		request.setName("");
		
		mockMvc.perform(
				patch("/api/v1/categories/" + (Long) category.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header("Authorization", "Bearer " + token)
		).andExpect(status().isBadRequest())
		.andDo(result -> {
			ErrorApiResponse response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			
			assertNotNull(response.getMessage());
			
			Category checkUpdatedCategory = categoryRepository.findFirstByName(request.getName()).orElse(null);
			assertNull(checkUpdatedCategory);
			
			Category checkOldCategory = categoryRepository.findFirstByName(category.getName()).orElse(null);
			assertNotNull(checkOldCategory);
		});
	}
	
	@Test
	void testUpdateFailedNotFound() throws Exception {
		UpdateCategoryRequest request = new UpdateCategoryRequest();
		request.setName("test");
		
		mockMvc.perform(
				patch("/api/v1/categories/10000")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header("Authorization", "Bearer " + token)
		).andExpect(status().isNotFound())
		.andDo(result -> {
			ErrorApiResponse response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			
			assertNotNull(response.getMessage());
		});
	}
	
	@Test
	void testDeleteSuccess() throws Exception {
		Category category = new Category();
		category.setName("test");
		categoryRepository.save(category);
		
		mockMvc.perform(
				delete("/api/v1/categories/" + (Long) category.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token)
		).andExpect(status().isOk())
		.andDo(result -> {
			SuccessApiResponse<String> response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			
			assertNotNull(response.getData());
			
			Category checkCategory = categoryRepository.findFirstByName(category.getName()).orElse(null);
			assertNull(checkCategory);
		});
	}
	
	@Test
	void testDeleteFailed() throws Exception {
		Category category = new Category();
		category.setName("test");
		categoryRepository.save(category);
		
		mockMvc.perform(
				delete("/api/v1/categories/1000000")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token)
		).andExpect(status().isNotFound())
		.andDo(result -> {
			ErrorApiResponse response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			
			assertNotNull(response.getMessage());
			
			Category checkCategory = categoryRepository.findFirstByName(category.getName()).orElse(null);
			assertNotNull(checkCategory);
		});
	}
	
}
