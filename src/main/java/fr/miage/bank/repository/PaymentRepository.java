package fr.miage.bank.repository;

import fr.miage.bank.entity.Cart;
import fr.miage.bank.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    List<Payment> getAllByCart_Id(String cartId);
    List<Payment> getAllByCart(Cart cart);
    Optional<Payment> findByIdAndCart(String id, Cart cart);
}