package fr.miage.bank.controller;

import fr.miage.bank.assembler.AccountAssembler;
import fr.miage.bank.entity.Account;
import fr.miage.bank.input.AccountInput;
import fr.miage.bank.validator.AccountValidator;
import fr.miage.bank.entity.User;
import fr.miage.bank.service.AccountService;
import fr.miage.bank.service.UserService;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.util.ReflectionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@ExposesResourceFor(Account.class)
@RequestMapping(value = "/users/{userId}/accounts")
public class AccountController {

    private final AccountService accountService;
    private final AccountAssembler assembler;
    private final AccountValidator validator;
    private final UserService userService;

    public AccountController(AccountService accountService, AccountAssembler assembler, AccountValidator validator,UserService userService) {
        this.accountService = accountService;
        this.assembler = assembler;
        this.validator = validator;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getAllAccountsByUserId(@PathVariable("userId") String userId) {
        Iterable<Account> allAccounts = accountService.findAllByUserId(userId);
        return ResponseEntity.ok(assembler.toCollectionModel(allAccounts));
    }

    @GetMapping(value = "/{accountId}")
    public ResponseEntity<?> getOneAccountById(@PathVariable("accountId") String userId, @PathVariable("accountId") String iban) {
        return Optional.ofNullable(accountService.findByIBAN(userId)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    @Transactional
    public ResponseEntity<?> saveAccount(@PathVariable("userId") String userId, @RequestBody @Valid AccountInput account) {
        Optional<User> optionUser = userService.findById(userId);
        Account account2save = new Account(
                account.getIBAN(),
                account.getSecret(),
                account.getCountry(),
                account.getSolde(),
                optionUser.get()
        );

        Account saved = accountService.createAccount(account2save);

        URI location = linkTo(AccountController.class).slash(saved.getIBAN()).toUri();
        return ResponseEntity.created(location).build();
    }


    @PutMapping(value = "/{accountId}")
    @Transactional
    public ResponseEntity<?> updateAccount(@PathVariable("userId") String userId, @RequestBody Account account, @PathVariable("accountId") String accountIBAN) {
        Optional<Account> body = Optional.ofNullable(account);

        if (!body.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        if (!accountService.existByIBAN(accountIBAN)) {
            return ResponseEntity.notFound().build();
        }

        account.setIBAN(accountIBAN);
        Account result = accountService.updateAccount(account);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "/{accountId}")
    @Transactional
    public ResponseEntity<?> updateAccountPartiel(@PathVariable("userId") String userId, @PathVariable("accountId") String accountIBAN,
                                                  @RequestBody Map<Object, Object> fields) {

        Optional<Account> body = accountService.findByUserIdAndIban(userId, accountIBAN);

        if (body.isPresent()) {
            Account account = body.get();

            fields.forEach((f, v) -> {
                Field field = ReflectionUtils.findField(Account.class, f.toString());
                field.setAccessible(true);

                if (field.getType() == Date.class) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.FRENCH);
                    try {
                        ReflectionUtils.setField(field, account, formatter.parse(v.toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    ReflectionUtils.setField(field, account, v);
                }
            });

            validator.validate(new AccountInput(account.getIBAN(), account.getSecret(),account.getCountry(), account.getSolde(),account.getOwner().getId()));
            account.setIBAN(accountIBAN);
            accountService.updateAccount(account);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}