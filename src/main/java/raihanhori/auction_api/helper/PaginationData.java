package raihanhori.auction_api.helper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginationData {
	
	private Long total_item;
	
	private int total_item_per_page;
	
	private int total_page;
	
	private int current_page;
}
