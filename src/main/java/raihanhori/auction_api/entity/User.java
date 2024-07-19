package raihanhori.auction_api.entity;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; 
	
	private String name;
	
	private String email;
	
	private String password;
	
	@Column(name = "is_verified")
	private boolean isVerified;
	
	@Enumerated(EnumType.STRING)
	private Role role;
	
	@CreatedDate()
	@Column(name = "created_at")
	private Instant createdAt;
	
	@LastModifiedDate()
	@Column(name = "updated_at")
	private Instant updatedAt;
	
	@OneToMany(mappedBy = "owner")
	private List<Product> products; 
	
	@OneToMany(mappedBy = "winnerUser")
	private List<Product> winnerProducts; 
	
}
