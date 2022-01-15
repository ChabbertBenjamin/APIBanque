package fr.miage.bank.controller;

import fr.miage.bank.assembler.OperationAssembler;
import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.Cart;
import fr.miage.bank.entity.Operation;
import fr.miage.bank.input.OperationInput;
import fr.miage.bank.repository.AccountRepository;
import fr.miage.bank.repository.CartRepository;
import fr.miage.bank.repository.OperationRepository;
import fr.miage.bank.validator.OperationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users/{userId}/accounts/{accountId}/operations")
public class OperationController {
    private final OperationRepository operationRepository;
    private final OperationAssembler assembler;
    private final OperationValidator validator;
    private final AccountRepository accountRepository;
    private final CartRepository cartRepository;


    @GetMapping
    //@PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> getAllOperationsByAccountId(@PathVariable("accountId") String accountId, @PathVariable String userId){
        Iterable<Operation> allOperations = operationRepository.findAllByCompteCreditor_IBAN(accountId);
        return ResponseEntity.ok(assembler.toCollectionModel(allOperations));
    }

    @GetMapping(value = "/{operationId}")
    //@PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> getOneOperationById(@PathVariable("accountId") String accountId, @PathVariable("operationId") String operationId, @PathVariable String userId){
        return Optional.ofNullable(operationRepository.findByIdAndCompteCreditor_IBAN(operationId, accountId)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categorie/{categoryName}")
    public ResponseEntity<?> getAllOperationsByAccountIdAndCategory(@PathVariable("userId") String userId, @PathVariable("accountId") String accountIban, @PathVariable("categoryName") String category){
        Iterable<Operation> allOperations;
        allOperations = operationRepository.findAllOperationsByCompteCreditor_IBANAndCategory(accountIban, category);
        return ResponseEntity.ok(assembler.toCollectionModel(allOperations));
    }

    @PostMapping
    @Transactional
    //@PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> createOperation(@RequestBody @Valid OperationInput operation, @PathVariable("accountId") String accountIBAN, @PathVariable String userId){
        Optional<Account> optionalAccountCred = accountRepository.findById(accountIBAN);
        Account accountCred = optionalAccountCred.get();

        Optional<Account> optionalAccountDeb = accountRepository.findById(operation.getDebitorAccount().getIBAN());
        Optional<Cart> optionalCart = cartRepository.findById(operation.getDebitorAccount().getIBAN());
        Account accountDeb = optionalAccountDeb.get();
        //Cart cart = optionalCart.get();
        if(accountDeb.getSolde() >= operation.getAmount()) {
            Operation operation2save = new Operation(
                    UUID.randomUUID().toString(),
                    new Timestamp(System.currentTimeMillis()),
                    operation.getText(),
                    operation.getAmount(),
                    operation.getTaux(),
                    accountCred,
                    accountDeb,
                    operation.getCategory(),
                    operation.getCountry()
            );

            Operation saved = operationRepository.save(operation2save);

            accountDeb.debiterCompte(operation.getAmount());
            accountCred.crediterCompte(operation.getAmount(), operation.getTaux());
            return ResponseEntity.ok(saved);
        }else {
            return ResponseEntity.badRequest().build();
        }
    }




}