package app.user.repository;


import app.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    @Query("SELECT COUNT(u) FROM User u WHERE SIZE(u.wallets) = :countWallets")
    long countByCountWallet(int countWallets);


}
