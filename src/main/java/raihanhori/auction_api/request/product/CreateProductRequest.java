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
public class CreateProductRequest {
	
	@JsonIgnore
	private Long userId;
	
	@NotNull
	private Long categoryId;
	
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
	
	@NotBlank
	private String currency;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotNull
	private Date endAuctionDate;
	
	@NotNull
	private MultipartFile[] images;
	
}
