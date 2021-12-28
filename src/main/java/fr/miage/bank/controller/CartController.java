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
@RequestMapping(value = "/accounts/{accountId}/cartes")
public class CartController {
    private final CartServices cartServices;
    private final AccountService accountService;
    private final CartAssembler assembler;


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

    @PostMapping
    @Transactional
    public ResponseEntity<?> createCarte(@RequestBody @Valid CartInput carte, @PathVariable("accountId") String accountId){
        Optional<Account> optionalAccount = accountService.findById(accountId);

        Account account = optionalAccount.get();
        Cart carte2save = new Cart(
                UUID.randomUUID().toString(),
                Integer.parseInt(carte.getCode()),
                Integer.parseInt(carte.getCrypto()),
                carte.isBloque(),
                carte.isLocalisation(),
                carte.getPlafond(),
                carte.isSansContact(),
                carte.isVirtual(),
                account
        );

        Cart saved = cartServices.createCart(carte2save);

        //Link location = linkTo(CarteController.class).slash(saved.getId()).slash(accountId).withSelfRel();
        //return ResponseEntity.ok(location.withSelfRel());

        return ResponseEntity.ok(saved);
    }

    @PutMapping(value = "/{carteId}")
    @Transactional
    public ResponseEntity<?> updateCarte(@RequestBody Cart carte, @PathVariable("carteId") String carteId){
        Optional<Cart> body = Optional.ofNullable(carte);

        if(!body.isPresent()){
            return ResponseEntity.badRequest().build();
        }

        if(!cartServices.existById(carteId)){
            return ResponseEntity.notFound().build();
        }

        carte.setId(carteId);
        Cart result = cartServices.updateCarte(carte);

        return ResponseEntity.ok().build();
    }
}