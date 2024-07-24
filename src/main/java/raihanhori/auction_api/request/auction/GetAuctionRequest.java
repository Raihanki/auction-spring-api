package raihanhori.auction_api.request.auction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetAuctionRequest {

	public Integer page;
	
	public Integer limit;
	
}
