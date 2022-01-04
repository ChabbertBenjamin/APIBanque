package fr.miage.bank.service;

import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.Cart;
import fr.miage.bank.entity.Operation;
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

    public Iterable<Cart> findAllCarts(String id){
        return cRepository.findAllByAccount_IBAN(id);
    }

    public boolean existByIBAN(String id ){
        return aRepository.existsById(id);
    }

    public Iterable<Operation> findAllOperations(String id){
        return oRepository.findAllByCompteCreditor_IBAN(id);
    }

    public Account updateAccount(Account account){
        return aRepository.save(account);
    }

    public Account createAccount(Account account){
        return aRepository.save(account);
    }
}
