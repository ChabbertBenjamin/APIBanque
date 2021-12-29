package fr.miage.bank.repository;

import fr.miage.bank.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OperationRepository extends JpaRepository<Operation, String> {
    public Iterable<Operation> findAllByCompteOwner_IBAN(String id);
    public Optional<Operation> findByIdAndAndCompteOwner_IBAN(String operationId, String accountId);
}