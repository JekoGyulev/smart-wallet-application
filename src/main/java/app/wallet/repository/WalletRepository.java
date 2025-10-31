package app.wallet.repository;

import app.wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {
    List<Wallet> findWalletByOwnerUsername(String recipientUsername);

    @Query("FROM Wallet w WHERE w.owner.id = :ownerId AND w.isPrimary = :isPrimary ")
    Optional<Wallet> findByOwner_IdAndPrimary(@Param(value = "ownerId") UUID ownerId, @Param(value = "isPrimary") boolean isPrimary);
}
