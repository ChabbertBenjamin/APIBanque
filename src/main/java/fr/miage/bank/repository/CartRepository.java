package fr.miage.bank.repository;

import fr.miage.bank.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
    public Iterable<Cart> findAllByAccount_IBAN(String id);
    public Optional<Cart> findByIdAndAccount_IBAN(String carteId, String accountId);
}
