package fr.miage.bank.controller;

import fr.miage.bank.assembler.CartAssembler;
import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.Cart;
import fr.miage.bank.input.CartInput;
import fr.miage.bank.repository.AccountRepository;
import fr.miage.bank.repository.CartRepository;
import fr.miage.bank.validator.CartValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;


import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequiredArgsConstructor
@ExposesResourceFor(Cart.class)
@RequestMapping(value = "users/{userId}/accounts/{accountId}/cartes")
public class CartController {
    private final CartRepository cartRepository;
    private final AccountRepository accountRepository;
    private final CartAssembler assembler;
    private final CartValidator validator;


    @GetMapping
    //@PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> getAllCartsByAccountId(@PathVariable("accountId") String accountId, @PathVariable String userId){
        Iterable<Cart> allCarts = cartRepository.findAllByAccount_IBAN(accountId);
        return ResponseEntity.ok(assembler.toCollectionModel(allCarts));
    }

    @GetMapping(value = "/{carteId}")
    //@PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> getOneCartByIdAndAccountId(@PathVariable("accountId") String accountId, @PathVariable("carteId") String cartId, @PathVariable String userId){
        return Optional.ofNullable(cartRepository.findByIdAndAccount_IBAN(cartId, accountId)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    //@PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> createCart(@RequestBody @Valid CartInput cart, @PathVariable("userId") String userId, @PathVariable("accountId") String accountId){
        Optional<Account> optionalAccount = accountRepository.findById(accountId);

        Account account = optionalAccount.get();
        Cart cart2save = new Cart(
                UUID.randomUUID().toString(),
                cart.getCode(),
                cart.getCrypto(),
                cart.isBloque(),
                cart.isLocalisation(),
                cart.getPlafond(),
                cart.isSansContact(),
                cart.isVirtual(),
                account
        );

        Cart saved = cartRepository.save(cart2save);

        //Link location = linkTo(CarteController.class).slash(saved.getId()).slash(accountId).withSelfRel();
        //return ResponseEntity.ok(location.withSelfRel());

        return ResponseEntity.ok(saved);
    }

    @PutMapping(value = "/{carteId}")
    @Transactional
    //@PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> updateCart(@RequestBody Cart cart, @PathVariable("carteId") String cartId, @PathVariable String accountId, @PathVariable String userId){
        Optional<Cart> body = Optional.ofNullable(cart);

        if(!body.isPresent()){
            return ResponseEntity.badRequest().build();
        }

        if(!cartRepository.existsById(cartId)){
            return ResponseEntity.notFound().build();
        }

        cart.setId(cartId);
        Cart result = cartRepository.save(cart);

        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "/{carteId}")
    @Transactional
    //@PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> updateCartePartial(@PathVariable("accountId") String accountIBAN,
                                                @PathVariable("carteId") String cartId,
                                                @RequestBody Map<Object, Object> fields){
        Optional<Cart> body = cartRepository.findByIdAndAccount_IBAN(cartId, accountIBAN);

        if(body.isPresent()){
            Cart cart = body.get();

            fields.forEach((f,v) -> {
                Field field = ReflectionUtils.findField(Cart.class, f.toString());
                field.setAccessible(true);

                if(field.getType() == Date.class){
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.FRENCH);
                    try {
                        ReflectionUtils.setField(field, cart, formatter.parse(v.toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else{
                    ReflectionUtils.setField(field, cart, v);
                }
            });

            validator.validate(new CartInput(cart.getCode(), cart.getCrypto(), cart.isFreeze(),
                    cart.isLocalisation(), cart.getPlafond(), cart.isContactLess(),
                    cart.isVirtual()));

            cart.setId(cartId);
            cartRepository.save(cart);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "/{carteId}")
    @Transactional
    //@PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> deleteCarte(@PathVariable("userId") String userId, @PathVariable("accountId") String accountIban, @PathVariable("carteId") String cartId){
        Optional<Cart> cart = cartRepository.findByIdAndAccount_IBAN(cartId, accountIban);
        if(cart.isPresent()){
            cartRepository.delete(cart.get());
        }

        return ResponseEntity.noContent().build();
    }
}