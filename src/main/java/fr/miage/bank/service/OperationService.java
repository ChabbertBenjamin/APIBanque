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


    public Iterable<Operation> findAllOperations(String id){
        return oRepository.findAllByCompteOwner_Id(id);
    }

    public Optional<Operation> findOperationById(String id){
        return oRepository.findById(id);
    }
}
