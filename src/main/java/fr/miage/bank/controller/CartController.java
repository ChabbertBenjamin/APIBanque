package fr.miage.bank.controller;

import fr.miage.bank.assembler.CartAssembler;
import fr.miage.bank.entity.Cart;
import fr.miage.bank.service.CartServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(value = "/accounts/{accountId}/cartes")
public class CartController {
    private final CartServices cartServices;
    private final CartAssembler assembler;

    public CartController(CartServices cartServices, CartAssembler assembler) {
        this.cartServices = cartServices;
        this.assembler = assembler;
    }

    @GetMapping
    public ResponseEntity<?> getAllCartsByAccountId(@PathVariable("accountId") String accountId){
        Iterable<Cart> allCartes = cartServices.findAllCartesByAccountId(accountId);
        return ResponseEntity.ok(assembler.toCollectionModel(allCartes));
    }
    @GetMapping(value = "/{carteId}")
    public ResponseEntity<?> getOneCarteByIdAndAccountId(@PathVariable("accountId") String accountId, @PathVariable("carteId") String carteId){
        return Optional.ofNullable(cartServices.findByIdAndAccountId(carteId, accountId)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }
}