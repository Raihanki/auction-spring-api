package raihanhori.auction_api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

	@NotBlank
	@Size(max = 200)
	private String name;
	
	@Email
	@NotBlank
	@Size(max = 200)
	private String email;
	
	@NotBlank
	@Size(min = 8, max = 100)
	private String password;
	
	@NotBlank
	private String role;
	
}
