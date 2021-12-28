package fr.miage.bank.controller;

import fr.miage.bank.assembler.CartAssembler;
import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.Cart;
import fr.miage.bank.entity.CartInput;
import fr.miage.bank.service.AccountService;
import fr.miage.bank.service.CartServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
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
        Optional<Account> account = accountService.findById(accountId);

        Cart carte2save = new Cart(
                UUID.randomUUID().toString(),
                Integer.parseInt(carte.getCode()),
                Integer.parseInt(carte.getCrypto()),
                carte.isBloque(),
                carte.isLocalisation(),
                carte.getPlafond(),
                carte.isSansContact(),
                carte.isVirtual(),
                account.get()
        );

        Cart saved = cartServices.createCart(carte2save);

        URI location = linkTo(CartController.class).slash(saved.getId()).toUri();
        return ResponseEntity.created(location).build();
    }
}