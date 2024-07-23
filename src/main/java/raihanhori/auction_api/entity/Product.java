package raihanhori.auction_api.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "products")
@EntityListeners(AuditingEntityListener.class)
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User owner;
	
	@ManyToOne
	@JoinColumn(name = "category_id", referencedColumnName = "id")
	private Category category;
	
	@ManyToOne
	@JoinColumn(name = "winner_user_id", referencedColumnName = "id")
	private User winnerUser;
	
	private String name;
	
	private String description;
	
	@Column(name = "start_price")
	private BigDecimal startPrice;
	
	@Column(name = "price_multiples")
	private BigDecimal priceMultiples;
	
	@Column(name = "end_price")
	private BigDecimal endPrice;
	
	@Enumerated(EnumType.STRING)
	private Currency currency;
	
	@Column(name = "end_auction_date")
	private Timestamp endAuctionDate;
	
	@CreatedDate()
	@Column(name = "created_at")
	private Instant createdAt;
	
	@LastModifiedDate()
	@Column(name = "updated_at")
	private Instant updatedAt;
	
	@OneToMany(mappedBy = "product")
	private List<Image> images;
	
	@OneToMany(mappedBy = "product")
	private List<Auction> auctions;
	
}

