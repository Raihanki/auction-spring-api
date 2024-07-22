package raihanhori.auction_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import raihanhori.auction_api.helper.SuccessApiResponse;
import raihanhori.auction_api.request.LoginRequest;
import raihanhori.auction_api.request.RegisterRequest;
import raihanhori.auction_api.response.AuthResponse;
import raihanhori.auction_api.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	@Autowired
	private AuthService authService;
	
	@PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public SuccessApiResponse<AuthResponse> login(@RequestBody LoginRequest request) {
		AuthResponse response = authService.login(request);
		
		return SuccessApiResponse.<AuthResponse>builder()
				.status(200)
				.data(response)
				.build();
	}
	
	@ResponseStatus(code = HttpStatus.CREATED)
	@PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public SuccessApiResponse<AuthResponse> register(@RequestBody RegisterRequest request) {
		AuthResponse response = authService.register(request);
		
		return SuccessApiResponse.<AuthResponse>builder()
				.status(201)
				.data(response)
				.build();
	}
	
}
