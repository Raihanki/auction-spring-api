package raihanhori.auction_api.service;

import raihanhori.auction_api.request.LoginRequest;
import raihanhori.auction_api.request.RegisterRequest;
import raihanhori.auction_api.response.AuthResponse;

public interface AuthService {

	AuthResponse login(LoginRequest request);
	
	AuthResponse register(RegisterRequest request);
	
}
