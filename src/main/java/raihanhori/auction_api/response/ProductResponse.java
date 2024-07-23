package raihanhori.auction_api.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import raihanhori.auction_api.entity.Currency;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {
	
	private Long id;
	
	private UserResponse user;
	
	private CategoryResponse category;
	
	private UserResponse winnerUser;
	
	private String name;
	
	private String description;
	
	private BigDecimal startPrice;
	
	private BigDecimal priceMultiples;
	
	private BigDecimal endPrice;
	
	private Currency currency;
	
	private Date endAuctionDate;
	
	private Instant createdAt;
	
	private List<ImageResponse> images;
	
}
