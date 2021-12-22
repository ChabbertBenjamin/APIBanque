package fr.miage.bank.service;

import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.Cart;
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

    public Iterable<Account> findAll(){
        return aRepository.findAll();
    }

    public Optional<Account> findById(String id){
        return aRepository.findById(id);
    }

    public Iterable<Cart> findAllCarts(String id){
        return cRepository.findAllByAccount_Id(id);
    }

    public boolean existById(String id ){
        return aRepository.existsById(id);
    }

    public Account updateAccount(Account account){
        return aRepository.save(account);
    }

    public Account createAccount(Account account){
        return aRepository.save(account);
    }
}
