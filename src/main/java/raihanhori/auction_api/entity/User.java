package raihanhori.auction_api.entity;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {

	private static final long serialVersionUID = 1L;

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
	
	@OneToMany(mappedBy = "user")
	private List<Auction> auctions;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(this.role.name()));
	}

	@Override
	public String getUsername() {
		return this.email;
	}
	
}
