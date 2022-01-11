package fr.miage.bank.repository;

import fr.miage.bank.entity.Cart;
import fr.miage.bank.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OperationRepository extends JpaRepository<Operation, String> {
    Iterable<Operation> findAllByCompteCreditor_IBAN(String id);
    Optional<Operation> findByIdAndCompteCreditor_IBAN(String operationId, String accountId);
    List<Operation> getAllByCartIdAndCompteCreditor_IBAN(String cartId, String accountId);
    //Optional<Operation> findByIdAndCartId(String id, Cart cart);
}