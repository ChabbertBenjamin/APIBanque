package fr.miage.bank.service;

import fr.miage.bank.entity.Cart;
import fr.miage.bank.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServices {

    private final CartRepository cRepository;

    public Iterable<Cart> findAllCartsByAccountId(String accountId){
        return cRepository.findAllByAccount_IBAN(accountId);
    }

    public Optional<Cart> findByIdAndAccountId(String cartId, String accountId){
        return cRepository.findByIdAndAccount_IBAN(cartId, accountId);
    }

    public Cart createCart(Cart cart){
        return cRepository.save(cart);
    }

    public boolean existById(String id){
        return cRepository.existsById(id);
    }
    public Cart updateCart(Cart cart){
        return cRepository.save(cart);
    }

}