package fr.miage.bank.controller;

import fr.miage.bank.assembler.CartAssembler;
import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.Cart;
import fr.miage.bank.entity.CartInput;
import fr.miage.bank.service.AccountService;
import fr.miage.bank.service.CartServices;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;


import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@ExposesResourceFor(Cart.class)
@RequestMapping(value = "users/{userId}/accounts/{accountId}/cartes")
public class CartController {
    private final CartServices cartServices;
    private final AccountService accountService;
    private final CartAssembler assembler;


    @GetMapping
    public ResponseEntity<?> getAllCartsByAccountId(@PathVariable("accountId") String accountId){
        Iterable<Cart> allCarts = cartServices.findAllCartsByAccountId(accountId);
        return ResponseEntity.ok(assembler.toCollectionModel(allCarts));
    }

    @GetMapping(value = "/{carteId}")
    public ResponseEntity<?> getOneCartByIdAndAccountId(@PathVariable("accountId") String accountId, @PathVariable("carteId") String cartId){
        return Optional.ofNullable(cartServices.findByIdAndAccountId(cartId, accountId)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> createCart(@RequestBody @Valid CartInput cart, @PathVariable("userId") String userId, @PathVariable("accountId") String accountId){
        Optional<Account> optionalAccount = accountService.findByIBAN(accountId);

        Account account = optionalAccount.get();
        Cart cart2save = new Cart(
                UUID.randomUUID().toString(),
                Integer.parseInt(cart.getCode()),
                Integer.parseInt(cart.getCrypto()),
                cart.isBloque(),
                cart.isLocalisation(),
                cart.getPlafond(),
                cart.isSansContact(),
                cart.isVirtual(),
                account
        );

        Cart saved = cartServices.createCart(cart2save);

        //Link location = linkTo(CarteController.class).slash(saved.getId()).slash(accountId).withSelfRel();
        //return ResponseEntity.ok(location.withSelfRel());

        return ResponseEntity.ok(saved);
    }

    @PutMapping(value = "/{carteId}")
    @Transactional
    public ResponseEntity<?> updateCart(@RequestBody Cart cart, @PathVariable("carteId") String cartId, @PathVariable String accountId, @PathVariable String userId){
        Optional<Cart> body = Optional.ofNullable(cart);

        if(!body.isPresent()){
            return ResponseEntity.badRequest().build();
        }

        if(!cartServices.existById(cartId)){
            return ResponseEntity.notFound().build();
        }

        cart.setId(cartId);
        Cart result = cartServices.updateCart(cart);

        return ResponseEntity.ok().build();
    }
}