package fr.miage.bank.controller;

import fr.miage.bank.assembler.OperationAssembler;
import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.Cart;
import fr.miage.bank.entity.DeviseConversionBean;
import fr.miage.bank.entity.Operation;
import fr.miage.bank.input.OperationInput;
import fr.miage.bank.repository.AccountRepository;
import fr.miage.bank.repository.CartRepository;
import fr.miage.bank.repository.OperationRepository;
import fr.miage.bank.validator.OperationValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/users/{userId}/accounts/{accountId}/operations")
public class OperationController {
    private final OperationRepository operationRepository;
    private final OperationAssembler assembler;
    private final OperationValidator validator;
    private final AccountRepository accountRepository;
    private final CartRepository cartRepository;
    RestTemplate template;

    public OperationController(OperationRepository operationRepository, OperationAssembler assembler, OperationValidator validator, AccountRepository accountRepository, CartRepository cartRepository, RestTemplate rt) {
        this.operationRepository = operationRepository;
        this.assembler = assembler;
        this.validator = validator;
        this.accountRepository = accountRepository;
        this.cartRepository = cartRepository;
        this.template = rt;
    }


    @GetMapping
    //@PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> getAllOperationsByAccountId(@PathVariable("accountId") String accountId, @PathVariable String userId) {
        Iterable<Operation> allOperations = operationRepository.findAllByCompteCreditor_IBAN(accountId);
        return ResponseEntity.ok(assembler.toCollectionModel(allOperations));
    }

    @GetMapping(value = "/{operationId}")
    //@PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> getOneOperationById(@PathVariable("accountId") String accountId, @PathVariable("operationId") String operationId, @PathVariable String userId) {
        return Optional.ofNullable(operationRepository.findByIdAndCompteCreditor_IBAN(operationId, accountId)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categorie/{categoryName}")
    public ResponseEntity<?> getAllOperationsByAccountIdAndCategory(@PathVariable("userId") String userId, @PathVariable("accountId") String accountIban, @PathVariable("categoryName") String category) {
        Iterable<Operation> allOperations;
        allOperations = operationRepository.findAllOperationsByCompteCreditor_IBANAndCategory(accountIban, category);
        return ResponseEntity.ok(assembler.toCollectionModel(allOperations));
    }

    @PostMapping
    @Transactional
    //@PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> createOperation(@RequestBody @Valid OperationInput operation, @PathVariable("accountId") String accountIBAN, @PathVariable String userId) {
        Optional<Account> optionalAccountDeb = accountRepository.findById(accountIBAN);
        Account accountDeb = optionalAccountDeb.get();

        Optional<Account> optionalAccountCred = accountRepository.findById(operation.getCreditorAccount().getIBAN());
        Account accountCred = optionalAccountCred.get();

        String paysDeb = accountDeb.getCountry();
        String source = "";
        switch (paysDeb) {
            case "Etats-Unis":
                source = "USD";
                break;
            case "Angleterre":
                source = "LIV";
                break;
            default:
                source = "EUR";
                break;
        }
        String paysCred = accountCred.getCountry();
        String cible = "";
        switch (paysCred) {
            case "Etats-Unis":
                cible = "USD";
                break;
            case "Angleterre":
                cible = "LIV";
                break;
            default:
                cible = "EUR";
                break;
        }
        System.out.println(source);
        System.out.println(cible);
        System.out.println(paysDeb);
        System.out.println(paysCred);
        if (!Objects.equals(paysDeb, paysCred)) {
            String url = "http://localhost:8000/taux-devise/source/{source}/cible/{cible}";
            DeviseConversionBean response = template.getForObject(url, DeviseConversionBean.class, source, cible);
            System.out.println(operation.getAmount());
            DeviseConversionBean dvb = new DeviseConversionBean(response.getId(), source, cible, response.getTauxConversion(), operation.getAmount(),
                    operation.getAmount().multiply(response.getTauxConversion()), response.getPort());

            System.out.println(dvb.getTotal());
            operation.setAmount(dvb.getTotal());
        }
            //Cart cart = optionalCart.get();
        System.out.println("IBAN DEBITEUR" + accountDeb.getIBAN());
        System.out.println("IBAN CREDITEUR" + accountCred.getIBAN());
            if (accountDeb.getSolde() >= operation.getAmount().doubleValue()) {
                Operation operation2save = new Operation(
                        UUID.randomUUID().toString(),
                        new Timestamp(System.currentTimeMillis()),
                        operation.getText(),
                        operation.getAmount(),
                        operation.getTaux(),
                        accountDeb,
                        accountCred,
                        operation.getCategory(),
                        operation.getCountry()
                );

                Operation saved = operationRepository.save(operation2save);
                accountDeb.debiterCompte(operation.getAmount().doubleValue());
                accountCred.crediterCompte(operation.getAmount().doubleValue(), operation.getTaux());

                URI location = linkTo(methodOn(OperationController.class).getOneOperationById(saved.getCompteDebitor().getIBAN(), saved.getId(), userId)).toUri();
                System.out.println(location);
                return ResponseEntity.created(location).build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        }




}