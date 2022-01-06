package fr.miage.bank.service;

import fr.miage.bank.entity.Operation;
import fr.miage.bank.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OperationService {

    private final OperationRepository oRepository;

    public Iterable<Operation> findAllOperationsByAccountId(String accountId){
        return oRepository.findAllByCompteCreditor_IBAN(accountId);
    }
    public Optional<Operation> findByIdAndCompteOwnerId(String operationId, String accountId){
        return oRepository.findByIdAndCompteCreditor_IBAN(operationId, accountId);
    }
    public Operation createOperation(Operation operation){
        return oRepository.save(operation);
    }
}