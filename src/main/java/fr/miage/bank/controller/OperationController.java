package fr.miage.bank.controller;

import fr.miage.bank.assembler.OperationAssembler;
import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.Cart;
import fr.miage.bank.entity.Operation;
import fr.miage.bank.entity.User;
import fr.miage.bank.input.OperationInput;
import fr.miage.bank.repository.AccountRepository;
import fr.miage.bank.repository.CartRepository;
import fr.miage.bank.repository.OperationRepository;
import fr.miage.bank.validator.OperationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final AccountRepository accountService;
    private final CartRepository cartRepository;


    @GetMapping
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> getAllOperationsByAccountId(@PathVariable("accountId") String accountId, @PathVariable String userId){
        Iterable<Operation> allOperations = operationRepository.findAllByCompteCreditor_IBAN(accountId);
        return ResponseEntity.ok(assembler.toCollectionModel(allOperations));
    }

    @GetMapping(value = "/carte/{carteId}")
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> getAllOperationByCartId(@PathVariable("carteId") String cartId, @PathVariable("accountId") String accountId, @PathVariable("userId") String userId){
        Iterable<Operation> allOperationCartId = operationRepository.getAllByCartIdAndCompteCreditor_IBAN(cartId, accountId);
        return ResponseEntity.ok(allOperationCartId);
    }

    @GetMapping(value = "/{operationId}")
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> getOneOperationById(@PathVariable("accountId") String accountId, @PathVariable("operationId") String operationId, @PathVariable String userId){
        return Optional.ofNullable(operationRepository.findByIdAndCompteCreditor_IBAN(operationId, accountId)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }



    @PostMapping
    @Transactional
    //@PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> createOperation(@RequestBody @Valid OperationInput operation, @PathVariable("accountId") String accountIBAN, @PathVariable String userId){
        Optional<Account> optionalAccountCred = accountService.findById(accountIBAN);
        Account accountCred = optionalAccountCred.get();

        Optional<Account> optionalAccountDeb = accountService.findById(operation.getDebtorAccountId());
        Optional<Cart> optionalCart = cartRepository.findById(operation.getDebtorAccountId());
        Account accountDeb = optionalAccountDeb.get();
        Cart cart = optionalCart.get();
        Operation operation2save = new Operation(
                UUID.randomUUID().toString(),
                new Timestamp(System.currentTimeMillis()),
                operation.getText(),
                operation.getAmount(),
                operation.getTaux(),
                accountCred,
                accountDeb,
                operation.getNameCreditor(),
                operation.getCategory(),
                operation.getCategory(),
                cart
        );

        Operation saved = operationRepository.save(operation2save);

        return ResponseEntity.ok(saved);
    }




}