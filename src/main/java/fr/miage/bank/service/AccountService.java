package fr.miage.bank.service;

import fr.miage.bank.entity.Account;
import fr.miage.bank.repository.AccountRepository;
import fr.miage.bank.repository.CartRepository;
import fr.miage.bank.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository aRepository;
    private final CartRepository cRepository;
    private final OperationRepository oRepository;

    public Iterable<Account> findAllByUserId(String userId){
        return aRepository.findAllByOwner_Id(userId);
    }

    public Optional<Account> findByUserIdAndIban(String userId, String iban){
        return aRepository.findByOwner_IdAndIBAN(userId, iban);
    }

    public Optional<Account> findByIBAN(String id){
        return aRepository.findById(id);
    }

    public boolean existByIBAN(String id ){
        return aRepository.existsById(id);
    }

    public Account updateAccount(Account account){
        return aRepository.save(account);
    }

    public Account createAccount(Account account){
        return aRepository.save(account);
    }

    public Optional<Account> findByIban(String iban){
        return aRepository.findById(iban);
    }
}
