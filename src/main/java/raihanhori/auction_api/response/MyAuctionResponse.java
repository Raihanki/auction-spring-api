package raihanhori.auction_api.response;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyAuctionResponse {

	private Long userId;
	
	private String productName;
	
	private Long productId;
	
	private BigDecimal price;
	
	private Instant createdAt;
	
}
