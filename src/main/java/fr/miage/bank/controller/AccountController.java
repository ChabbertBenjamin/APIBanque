package fr.miage.bank.controller;

import fr.miage.bank.assembler.AccountAssembler;
import fr.miage.bank.entity.Account;
import fr.miage.bank.input.AccountInput;
import fr.miage.bank.repository.AccountRepository;
import fr.miage.bank.repository.UserRepository;
import fr.miage.bank.validator.AccountValidator;
import fr.miage.bank.entity.User;
import lombok.RequiredArgsConstructor;
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
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@ExposesResourceFor(Account.class)
@RequiredArgsConstructor
@RequestMapping(value = "/users/{userId}/accounts")
public class AccountController {

    private final AccountRepository accountRepository;
    private final AccountAssembler assembler;
    private final AccountValidator validator;
    private final UserRepository userRepository;

    @GetMapping
    //@PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> getAllAccountsByUserId(@PathVariable("userId") String userId) {
        Iterable<Account> allAccounts = accountRepository.findAllByOwner_Id(userId);
        return ResponseEntity.ok(assembler.toCollectionModel(allAccounts));
    }

    @GetMapping(value = "/{accountId}")
    //@PreAuthorize("hasPermission(#accountId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> getOneAccountById(@PathVariable("userId") String userId, @PathVariable("accountId") String accountId) {
        return Optional.of(accountRepository.findById(accountId)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    @Transactional
    //@PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> saveAccount(@PathVariable("userId") String userId, @RequestBody @Valid AccountInput account) {
        Optional<User> optionUser = userRepository.findById(userId);
        Account account2save = new Account(
                account.getIBAN(),
                account.getSecret(),
                account.getCountry(),
                account.getSolde(),
                optionUser.get()
        );

        Account saved = accountRepository.save(account2save);
        URI location = linkTo(methodOn(AccountController.class).getOneAccountById(userId, saved.getIBAN())).toUri();
        return ResponseEntity.created(location).build();

    }


    @PutMapping(value = "/{accountId}")
    @Transactional
    //@PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> updateAccount(@PathVariable("userId") String userId, @RequestBody Account account, @PathVariable("accountId") String accountIBAN) {
        Optional<Account> body = Optional.ofNullable(account);

        if (!body.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        if (!accountRepository.existsById(accountIBAN)) {
            return ResponseEntity.notFound().build();
        }

        account.setIBAN(accountIBAN);
        Account result = accountRepository.save(account);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "/{accountId}")
    @Transactional
    //@PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> updateAccountPartiel(@PathVariable("userId") String userId, @PathVariable("accountId") String accountIBAN,
                                                  @RequestBody Map<Object, Object> fields) {

        Optional<Account> body = accountRepository.findByOwner_IdAndIBAN(userId, accountIBAN);

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
            accountRepository.save(account);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}