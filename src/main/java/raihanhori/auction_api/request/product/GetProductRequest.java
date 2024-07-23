package raihanhori.auction_api.request.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetProductRequest {

	private String search;
	
	private Integer limit;
	
	private Integer page;
	
}
