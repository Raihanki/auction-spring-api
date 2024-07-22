package raihanhori.auction_api;


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import raihanhori.auction_api.helper.SuccessApiResponse;
import raihanhori.auction_api.repository.UserRepository;
import raihanhori.auction_api.response.UserResponse;
import raihanhori.auction_api.security.JwtUtils;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
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
		user.setRole(Role.USER);
		userRepository.save(user);
		
		token = jwtUtils.generateToken(user);
	}
	
	@AfterEach
	void tearDown() {
		userRepository.deleteAll();
	}
	
	@Test
	void testSuccessGetUser() throws Exception {
		mockMvc.perform(
				get("/api/v1/users")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token)
		).andExpect(status().isOk())
		.andDo(result -> {
			SuccessApiResponse<UserResponse> response = 
					objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
			
			assertEquals("test@example.com",response.getData().getEmail());
			assertEquals("test",response.getData().getName());
		});
	}
	
	@Test
	void testFailedGetUserUnauthenticated() throws Exception {
		mockMvc.perform(
				get("/api/v1/users")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		).andExpect(status().isForbidden());
	}
	
}
