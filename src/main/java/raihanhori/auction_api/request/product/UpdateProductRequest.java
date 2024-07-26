package raihanhori.auction_api.request.product;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProductRequest {
	
	@JsonIgnore
	private Long id;
	
	@JsonIgnore
	private Long userId;
	
	@NotNull
	private Long categoryId;
	
	private Long winnerUserId;
	
	@NotBlank
	@Size(max = 200)
	private String name;
	
	@NotBlank
	@Size(max = 200)
	private String description;
	
	@NotNull
	@Min(value = 1)
	private BigDecimal startPrice;
	
	@NotNull
	@Min(value = 2)
	private BigDecimal priceMultiples;
	
	private BigDecimal endPrice;
	
	@NotBlank
	private String currency;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull
	private Date endAuctionDate;
	
	@NotNull
	private MultipartFile[] images;
	
}

