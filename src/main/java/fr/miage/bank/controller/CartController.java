package fr.miage.bank.controller;

import fr.miage.bank.assembler.CartAssembler;
import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.Cart;
import fr.miage.bank.entity.User;
import fr.miage.bank.input.CartInput;
import fr.miage.bank.repository.AccountRepository;
import fr.miage.bank.repository.CartRepository;
import fr.miage.bank.repository.UserRepository;
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
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@ExposesResourceFor(Cart.class)
@RequestMapping(value = "users/{userId}/accounts/{accountId}/cartes")
public class CartController {
    private final CartRepository cartRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final CartAssembler assembler;
    private final CartValidator validator;


    @GetMapping
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> getAllCartsByAccountId(@PathVariable("accountId") String accountId, @PathVariable String userId){
        Iterable<Cart> allCarts = cartRepository.findAllByAccount_IBAN(accountId);
        return ResponseEntity.ok(assembler.toCollectionModel(allCarts));
    }

    @GetMapping(value = "/{carteId}")
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> getOneCartByIdAndAccountId(@PathVariable("accountId") String accountId, @PathVariable("carteId") String cartId, @PathVariable String userId){
        return Optional.ofNullable(cartRepository.findByIdAndAccount_IBAN(cartId, accountId)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> createCart(@RequestBody @Valid CartInput cart, @PathVariable("userId") String userId, @PathVariable("accountId") String accountId){
        Optional<Account> optionalAccount = accountRepository.findById(accountId);
        Date expirationDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(expirationDate);
        if(cart.isVirtual()){
            // Virtuel pour 15 jours
            c.add(Calendar.DATE, 15);
        }else{
            // physique pour 3 ans
            c.add(Calendar.DATE, 365*3);
        }
        expirationDate = c.getTime();
        Account account = optionalAccount.get();
        Cart cart2save = new Cart(
                UUID.randomUUID().toString(),
                cart.getCode(),
                cart.getCrypto(),
                cart.isFreeze(),
                cart.isLocalisation(),
                cart.getPlafond(),
                cart.isContactLess(),
                cart.isVirtual(),
                expirationDate,
                cart.getNum(),
                account
        );
        Cart saved = cartRepository.save(cart2save);
        URI location = linkTo(methodOn(CartController.class).getOneCartByIdAndAccountId(accountId, saved.getId(), userId)).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping(value = "/{carteId}")
    @Transactional
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
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
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> updateCartePartial(@PathVariable("userId") String userId, @PathVariable("accountId") String accountId, @PathVariable("carteId") String cartId, @RequestBody Map<Object, Object> fields){
        Optional<Cart> body = cartRepository.findByIdAndAccount_IBAN(cartId, accountId);
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
                    cart.isVirtual(),cart.getDateExpiry(),cart.getNum()));
            cart.setId(cartId);
            cartRepository.save(cart);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "/{carteId}")
    @Transactional
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> deleteCarte(@PathVariable("userId") String userId, @PathVariable("accountId") String accountId, @PathVariable("carteId") String cartId){
        Optional<Cart> cart = cartRepository.findByIdAndAccount_IBAN(cartId, accountId);
        if(cart.isPresent()){
            cartRepository.delete(cart.get());
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{carteId}/freeze")
    @Transactional
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> blockCarte(@PathVariable("userId") String userId, @PathVariable("accountId") String accountId, @PathVariable("carteId") String cartId) {
        Cart cart = verifCart(userId,accountId,cartId);
        if(cart==null){
            return ResponseEntity.notFound().build();
        }else{
            cart.setFreeze(true);
            cartRepository.save(cart);
            return ResponseEntity.noContent().build();
        }

    }
    @PostMapping(value = "/{carteId}/activeLocalisation")
    @Transactional
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> activeLocalisation(@PathVariable("userId") String userId, @PathVariable("accountId") String accountId, @PathVariable("carteId") String cartId){
        Cart cart = verifCart(userId,accountId,cartId);
        if(cart==null){
            return ResponseEntity.notFound().build();
        }else {
            cart.setLocalisation(true);
            cartRepository.save(cart);
            return ResponseEntity.noContent().build();
        }
    }
    @PostMapping(value = "/{carteId}/desactiveLocalisation")
    @Transactional
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> desactiveLocalisation(@PathVariable("userId") String userId, @PathVariable("accountId") String accountId, @PathVariable("carteId") String cartId){
        Cart cart = verifCart(userId,accountId,cartId);
        if(cart==null){
            return ResponseEntity.notFound().build();
        }else {
            cart.setLocalisation(false);
            cartRepository.save(cart);
            return ResponseEntity.noContent().build();
        }
    }
    @PostMapping(value = "/{carteId}/setPlafond")
    @Transactional
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> setPlafond(@PathVariable("userId") String userId, @PathVariable("accountId") String accountId, @PathVariable("carteId") String cartId, @RequestParam("plafond") int plafond) {
        Cart cart = verifCart(userId,accountId,cartId);
        if(cart==null){
            return ResponseEntity.notFound().build();
        }else {
            cart.setPlafond(plafond);
            cartRepository.save(cart);
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping(value = "/{carteId}/setContactLess")
    @Transactional
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> setSansContact(@PathVariable("userId") String userId, @PathVariable("accountId") String accountId, @PathVariable("carteId") String cartId){
        Cart cart = verifCart(userId,accountId,cartId);
        if(cart==null){
            return ResponseEntity.notFound().build();
        }else {
            cart.setContactLess(true);
            cartRepository.save(cart);
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping(value = "/{carteId}/unsetContactLess")
    @Transactional
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> unsetSansContact(@PathVariable("userId") String userId, @PathVariable("accountId") String accountId, @PathVariable("carteId") String cartId){
        Cart cart = verifCart(userId,accountId,cartId);
        if(cart==null){
            return ResponseEntity.notFound().build();
        }else {
            cart.setContactLess(false);
            cartRepository.save(cart);
            return ResponseEntity.noContent().build();
        }
    }

    // Retourne la carte si elle est valide, sinon retourne null
    public Cart verifCart(String userId, String accountId, String cartId){
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<Account> optionAccount = accountRepository.findByOwnerAndIBAN(optionalUser, accountId);
        if(!optionAccount.isPresent()){
            return null;
        }
        Optional<Cart> optionCarte = cartRepository.findByIdAndAccount_IBAN(cartId, accountId);
        return optionCarte.orElse(null);
    }
}