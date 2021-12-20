package fr.miage.bank.service;

import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.Cart;
import fr.miage.bank.repository.AccountRepository;
import fr.miage.bank.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository aRepository;
    private final CartRepository cRepository;

    public Iterable<Account> findAll(){
        return aRepository.findAll();
    }

    public Optional<Account> findById(long id){
        return aRepository.findById(id);
    }

    public Iterable<Cart> findAllCarts(long id){
        return cRepository.findAllByAccount_Id(id);
    }
}
