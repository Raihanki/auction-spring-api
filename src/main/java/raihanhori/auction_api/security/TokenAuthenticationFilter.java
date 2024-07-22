package raihanhori.auction_api.security;

import java.io.IOException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import raihanhori.auction_api.helper.ErrorApiResponse;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter{
	
	@Autowired
	private JwtUtils jwtUtils;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String token = jwtUtils.getTokenFromAuthorizationHeader(request);
		if (token == null) {
			filterChain.doFilter(request, response);
			return;
		}
		
		try {
			String username = jwtUtils.getUsernameFromToken(token);

			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			
			if (Objects.nonNull(userDetails) && jwtUtils.isTokenValid(token, userDetails)
					&& SecurityContextHolder.getContext().getAuthentication() == null) {
				
				UsernamePasswordAuthenticationToken	auth =
						new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				
				SecurityContextHolder.getContext().setAuthentication(auth);
			}
			
		} catch (JwtException exception) {
			response.setStatus(401);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			
			ErrorApiResponse errorResponse = ErrorApiResponse.builder()
					.status(401).message("Unauthorized")
					.build();
			
			objectMapper.writeValue(response.getWriter(), errorResponse);
			return;
		}
		
		filterChain.doFilter(request, response);
	}

}
