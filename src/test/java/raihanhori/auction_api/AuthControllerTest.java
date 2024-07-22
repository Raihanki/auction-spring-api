package raihanhori.auction_api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import raihanhori.auction_api.entity.Role;
import raihanhori.auction_api.entity.User;
import raihanhori.auction_api.helper.ErrorApiResponse;
import raihanhori.auction_api.helper.SuccessApiResponse;
import raihanhori.auction_api.repository.UserRepository;
import raihanhori.auction_api.request.LoginRequest;
import raihanhori.auction_api.request.RegisterRequest;
import raihanhori.auction_api.response.AuthResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@AfterEach
	void tearDown() {
		userRepository.deleteAll();
	}
	
	@Test
	void testRegisterSuccess() throws Exception {
		RegisterRequest request = new RegisterRequest();
		request.setEmail("test@example.com");
		request.setName("test");
		request.setRole("USER");
		request.setPassword("password");
		
		mockMvc.perform(
				post("/api/v1/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isCreated())
		.andDo(result -> {
			SuccessApiResponse<AuthResponse> response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			
			assertNotNull(response.getData().getToken());
			
			User user = userRepository.findFirstByEmail("test@example.com").orElse(null);
			assertNotNull(user);
		});
	
	}
	
	@Test
	void testRegisterFailedValidation() throws Exception {
		RegisterRequest request = new RegisterRequest();
		request.setEmail("test");
		request.setName("");
		request.setRole("USER");
		request.setPassword("");
		
		mockMvc.perform(
				post("/api/v1/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isBadRequest())
		.andDo(result -> {
			ErrorApiResponse response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			
			assertNotNull(response.getMessage());
		});
	
	}
	
	@Test
	void testRegisterFailedEmailExist() throws Exception {
		User user = new User();
		user.setEmail("test@example.com");
		user.setPassword("test");
		user.setName("test");
		userRepository.save(user);
		
		RegisterRequest request = new RegisterRequest();
		request.setEmail("test@example.com");
		request.setName("");
		request.setRole("USER");
		request.setPassword("");
		
		mockMvc.perform(
				post("/api/v1/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isBadRequest())
		.andDo(result -> {
			ErrorApiResponse response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			
			assertNotNull(response.getMessage());
		});
	
	}
	
	@Test
	void testLoginSuccess() throws Exception {
		User user = new User();
		user.setEmail("test@example.com");
		user.setPassword(passwordEncoder.encode("password"));
		user.setName("test");
		user.setRole(Role.USER);
		userRepository.save(user);
		
		LoginRequest request = new LoginRequest();
		request.setEmail("test@example.com");
		request.setPassword("password");
		
		mockMvc.perform(
				post("/api/v1/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isOk())
		.andDo(result -> {
			SuccessApiResponse<AuthResponse> response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			
			assertNotNull(response.getData().getToken());
		});
	}
	
	@Test
	void testLoginFailed() throws Exception {
		LoginRequest request = new LoginRequest();
		request.setEmail("failed@example.com");
		request.setPassword("failed");
		
		mockMvc.perform(
				post("/api/v1/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isBadRequest())
		.andDo(result -> {
			ErrorApiResponse response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			
			assertNotNull(response.getMessage());
		});
	}
	
}
