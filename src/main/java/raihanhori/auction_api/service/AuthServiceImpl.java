package raihanhori.auction_api.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import raihanhori.auction_api.entity.User;
import raihanhori.auction_api.helper.ValidationHelper;
import raihanhori.auction_api.repository.UserRepository;
import raihanhori.auction_api.request.LoginRequest;
import raihanhori.auction_api.request.RegisterRequest;
import raihanhori.auction_api.response.AuthResponse;
import raihanhori.auction_api.security.JwtUtils;

@Service
public class AuthServiceImpl implements AuthService {
	
	@Autowired
	private ValidationHelper validationHelper;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder encoder;
	
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtils jwtUtils;
	
	@Override
	public AuthResponse login(LoginRequest request) {
		validationHelper.validate(request);
		
		UsernamePasswordAuthenticationToken credentials = 
				new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
		
		try {
			authenticationManager.authenticate(credentials);
		} catch (AuthenticationException exception) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid username or password");
		}
		
		User user = userRepository.findFirstByEmail(request.getEmail()).orElseThrow(() -> 
			new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
		
		String token = jwtUtils.generateToken(user);
		Date expiredAt = jwtUtils.getTokenExpiredAt(token);
		
		return AuthResponse.builder().token(token).expriredAt(expiredAt)
				.build();
	}

	@Override
	public AuthResponse register(RegisterRequest request) {
		validationHelper.validate(request);
		
		if (userRepository.findFirstByEmail(request.getEmail()).isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email is already used");
		}
		
		User user = new User();
		user.setEmail(request.getEmail());
		user.setName(request.getName());
		user.setPassword(encoder.encode(request.getPassword()));
		user.setVerified(false);
		userRepository.save(user);
		
		String token = jwtUtils.generateToken(user);
		Date expiredAt = jwtUtils.getTokenExpiredAt(token);
		
		return AuthResponse.builder().token(token).expriredAt(expiredAt)
				.build();
	}

}
