package raihanhori.auction_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import raihanhori.auction_api.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

}