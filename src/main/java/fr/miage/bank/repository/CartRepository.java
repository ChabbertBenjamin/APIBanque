package fr.miage.bank.repository;

import fr.miage.bank.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    public Iterable<Cart> findAllByAccount_Id(long id);
}
