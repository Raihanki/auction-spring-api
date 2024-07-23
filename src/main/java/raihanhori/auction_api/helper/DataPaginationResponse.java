package raihanhori.auction_api.helper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataPaginationResponse<T> {
	
	private Integer status;

	private T data;
	
	private PaginationData meta;
	
}
