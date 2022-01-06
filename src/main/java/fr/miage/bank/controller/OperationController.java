package fr.miage.bank.controller;

import fr.miage.bank.assembler.OperationAssembler;
import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.Operation;
import fr.miage.bank.input.OperationInput;
import fr.miage.bank.service.AccountService;
import fr.miage.bank.service.OperationService;
import fr.miage.bank.validator.OperationValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/users/{userId}/accounts/{accountId}/operations")
public class OperationController {
    private final OperationService operationService;
    private final OperationAssembler assembler;
    private final OperationValidator validator;
    private final AccountService accountService;

    public OperationController(OperationService operationService, OperationAssembler assembler, OperationValidator validator, AccountService accountService) {
        this.operationService = operationService;
        this.assembler = assembler;
        this.validator = validator;
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<?> getAllOperationsByAccountId(@PathVariable("accountId") String accountId){
        Iterable<Operation> allOperations = operationService.findAllOperationsByAccountId(accountId);
        return ResponseEntity.ok(assembler.toCollectionModel(allOperations));
    }

    @GetMapping(value = "/{operationId}")
    public ResponseEntity<?> getOneOperationById(@PathVariable("accountId") String accountId, @PathVariable("operationId") String operationId){
        return Optional.ofNullable(operationService.findByIdAndCompteOwnerId(operationId, accountId)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> createOperation(@RequestBody @Valid OperationInput operation, @PathVariable("userId") String userId, @PathVariable("accountId") String accountIBAN){
        Optional<Account> optionalAccountCred = accountService.findByIban(accountIBAN);
        Account accountCred = optionalAccountCred.get();

        Optional<Account> optionalAccountDeb = accountService.findByIban(operation.getDebtorAccountId());
        Account accountDeb = optionalAccountDeb.get();

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
                operation.getCategory()
        );

        Operation saved = operationService.createOperation(operation2save);

        return ResponseEntity.ok(saved);
    }
}