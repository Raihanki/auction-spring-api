package raihanhori.auction_api.request.auction;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateAuctionRequest {

	@JsonIgnore
	private Long productId;
	
	@NotNull
	private BigDecimal price;
	
}
