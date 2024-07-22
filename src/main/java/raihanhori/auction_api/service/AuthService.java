package raihanhori.auction_api.service;

import raihanhori.auction_api.request.auth.LoginRequest;
import raihanhori.auction_api.request.auth.RegisterRequest;
import raihanhori.auction_api.response.AuthResponse;

public interface AuthService {

	AuthResponse login(LoginRequest request);
	
	AuthResponse register(RegisterRequest request);
	
}
