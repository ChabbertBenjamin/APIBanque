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

    public Iterable<Cart> findAllCartesByAccountId(String accountId){
        return cRepository.findAllByAccount_IBAN(accountId);
    }

    public Optional<Cart> findByIdAndAccountId(String carteId, String accountId){
        return cRepository.findByIdAndAccount_IBAN(carteId, accountId);
    }

    public Cart createCart(Cart carte){
        return cRepository.save(carte);
    }

    public boolean existById(String id){
        return cRepository.existsById(id);
    }
    public Cart updateCarte(Cart carte){
        return cRepository.save(carte);
    }

}