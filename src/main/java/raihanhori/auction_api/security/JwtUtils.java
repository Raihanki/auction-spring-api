package raihanhori.auction_api.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtils {
	
	@Value("${spring.security.jwt.secret-key}")
	private String secretKey;
	
	@Value("${spring.security.jwt.expired-in-ms}")
	private Integer tokenExpiredInMs;
	
	public String getTokenFromAuthorizationHeader(HttpServletRequest request) {
		String header = request.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer ")) {
			return header.substring(7);
		}
		
		return null;
	}
	
	public String generateToken(UserDetails userDetails) {
		return generateToken(new HashMap<>(), userDetails);
	}
	
	public String generateToken(Map<String, Object> claims, UserDetails userDetails) {
		return Jwts.builder()
					.subject(userDetails.getUsername())
					.claims(claims)
					.issuedAt(new Date(System.currentTimeMillis()))
					.expiration(new Date((System.currentTimeMillis() + tokenExpiredInMs)))
					.signWith(generateSecretKey())
					.compact();
	}
	
	public String getUsernameFromToken(String token) {
		return this.extractClaim(token, Claims::getSubject);
	}
	
	public Date getTokenExpiredAt(String token) {
		return this.extractClaim(token, Claims::getExpiration);
	}
	
	public boolean isTokenValid(String token, UserDetails userDetails) {
		String username = this.getUsernameFromToken(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}
	
	public boolean isTokenExpired(String token) {
		Date expiredAt = this.getTokenExpiredAt(token);
		return expiredAt.before(new Date(System.currentTimeMillis()));
	}
	
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractClaims(token);
		return claimsResolver.apply(claims);
	}
	
	private Claims extractClaims(String token) {
		return Jwts.parser().verifyWith(generateSecretKey()).build()
				.parseSignedClaims(token).getPayload();
	}
	
	private SecretKey generateSecretKey() {
		byte[] key = Decoders.BASE64.decode(this.secretKey);
		return Keys.hmacShaKeyFor(key);
	}
}
