package fr.miage.bank.controller;

import fr.miage.bank.assembler.AccountAssembler;
import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.Cart;
import fr.miage.bank.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;

@RestController
@RequestMapping(value = "/accounts")
public class AccountController {

    private final AccountService accountService;
    private final AccountAssembler assembler;

    public AccountController(AccountService accountService, AccountAssembler assembler) {
        this.accountService = accountService;
        this.assembler = assembler;
    }

    @GetMapping
    public ResponseEntity<?> getAllAccounts(){
        Iterable<Account> allAccounts = accountService.findAll();
        return ResponseEntity.ok(assembler.toCollectionModel(allAccounts));
    }

    @GetMapping(value = "/{accountId}")
    public ResponseEntity<?> getOneAccountById(@PathVariable("accountId") Long id){
        return Optional.ofNullable(accountService.findById(id)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/{accountId}/cartes")
    public ResponseEntity<?> getAllCarts(@PathVariable("accountId") Long id){
        Iterable<Cart> allCarts = accountService.findAllCarts(id);
        return ResponseEntity.ok(allCarts);
    }

}