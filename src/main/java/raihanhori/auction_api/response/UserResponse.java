package raihanhori.auction_api.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
	
	private Long id;
	
	private String email;
	
	private String name;
	
	private boolean isVerified;
	
	private Instant createdAt;
	
}
