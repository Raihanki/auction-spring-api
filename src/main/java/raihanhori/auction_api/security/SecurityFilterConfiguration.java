package raihanhori.auction_api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityFilterConfiguration {
	
	@Autowired
	private AuthenticationProvider authenticationProvider;
	
	@Autowired
	private TokenAuthenticationFilter tokenAuthenticationFilter;

	@Bean
	public SecurityFilterChain securityFilterchain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable());
		
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		
		http.authorizeHttpRequests(request -> {
			
			request.requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
			.requestMatchers(HttpMethod.POST, "/api/v1/categories").hasAuthority("ADMIN")
			.requestMatchers(HttpMethod.PATCH, "/api/v1/categories/*").hasAuthority("ADMIN")
			.requestMatchers(HttpMethod.DELETE, "/api/v1/categories/*").hasAuthority("ADMIN")
			.requestMatchers(HttpMethod.GET, "/api/v1/users").authenticated()
			.requestMatchers(HttpMethod.POST, "/api/v1/products/*").hasAuthority("OWNER")
			.requestMatchers(HttpMethod.DELETE, "/api/v1/products/*").hasAuthority("OWNER")
			.requestMatchers("/api/v1/products/{productId}/auctions/**").hasAnyAuthority("OWNER", "USER")
			
			.anyRequest().permitAll();
		});
		
		http.authenticationProvider(authenticationProvider);
		
		http.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
	
}
