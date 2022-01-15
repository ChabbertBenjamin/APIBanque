package fr.miage.bank.controller;

import fr.miage.bank.assembler.PaymentAssembler;
import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.Cart;
import fr.miage.bank.entity.DeviseConversionBean;
import fr.miage.bank.entity.Payment;
import fr.miage.bank.input.PaymentInput;
import fr.miage.bank.repository.AccountRepository;
import fr.miage.bank.repository.CartRepository;
import fr.miage.bank.repository.PaymentRepository;
import fr.miage.bank.validator.PaymentValidator;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@ExposesResourceFor(Payment.class)
@RequestMapping(value = "/users/{userId}/accounts/{accountIban}/cartes/{carteId}/paiements")
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final CartRepository cartRepository;
    private final AccountRepository accountRepository;

    RestTemplate template;

    private final PaymentAssembler assembler;
    private final PaymentValidator validator;

    public PaymentController(PaymentRepository paymentRepository, CartRepository cartRepository, AccountRepository accountRepository, PaymentAssembler assembler, PaymentValidator validator, RestTemplate rt) {
        this.paymentRepository = paymentRepository;
        this.cartRepository = cartRepository;
        this.accountRepository = accountRepository;
        this.assembler = assembler;
        this.validator = validator;
        this.template = rt;
    }

    @GetMapping
    //@PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> getAllPaiementsByCarteId(@PathVariable("userId") String userId, @PathVariable("accountIban") String iban, @PathVariable("carteId") String carteId){
        Iterable<Payment> allPaiements = paymentRepository.getAllByCart_Id(carteId);
        return ResponseEntity.ok(allPaiements);
    }

    @GetMapping(value = "/{paiementId}")
    //@PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> getOnePaiementById(@PathVariable("userId") String userId, @PathVariable("accountIban") String accountId, @PathVariable("carteId") String carteId,
                                                @PathVariable("paiementId") String paiementId){
        Optional<Cart> optionalCarte = cartRepository.findByIdAndAccount_IBAN(carteId, accountId);
        return Optional.ofNullable(paymentRepository.findByIdAndCart(paiementId, optionalCarte.get())).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    //@PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> createPaiement(@RequestBody @Valid PaymentInput paiement, @PathVariable("userId") String userId, @PathVariable("accountIban") String accountIban,
                                            @PathVariable("carteId") String carteId){



        Optional<Cart> optionalCart = cartRepository.findByIdAndAccount_IBAN(carteId, accountIban);
        Cart cart = optionalCart.get();
        Account compteDeb = cart.getAccount();
        Optional<Account> optionalAccount = accountRepository.findById(paiement.getIbanCreditor());
        Account compteCred = optionalAccount.get();

        Date today = new Date();
        /*
        if(today.after(cart.getDateExpiry()) || cart.isFreeze()){
            return ResponseEntity.badRequest().build();
        }*/
        System.out.println(cart.isLocalisation());
        if(cart.isLocalisation()){
            String paysDeb = compteDeb.getCountry();
            String source = "";
            switch (paysDeb){
                case "France","Belgique","Italie","Espagne":
                    source="EUR";
                    break;
                case "Etats-Unis":
                    source="USD";
                    break;
                case "Angleterre":
                    source="LIV";
                    break;
                default:
                    source="EUR";
                    break;
            }
            String cible = "";
            switch (paysDeb){
                case "France","Belgique","Italie","Espagne":
                    cible="EUR";
                    break;
                case "Etats-Unis":
                    cible="USD";
                    break;
                case "Angleterre":
                    cible="LIV";
                    break;
                default:
                    cible="EUR";
                    break;
            }
            String paysCred = compteCred.getCountry();
            System.out.println(paysDeb);
            System.out.println(paysCred);
            if(!Objects.equals(paysDeb, paysCred)){
                String url = "http://localhost:8000/taux-devise/source/{source}/cible/{cible}";
                DeviseConversionBean response = template.getForObject(url, DeviseConversionBean.class, source, cible);
                System.out.println(paiement.getAmount());
                System.out.println(response);
                DeviseConversionBean dvb = new DeviseConversionBean(response.getId(), source, cible, response.getTauxConversion(), paiement.getAmount(),
                        paiement.getAmount().multiply(response.getTauxConversion()), response.getPort());

                System.out.println(dvb.getTotal());
                paiement.setAmount(dvb.getTotal());
            }
        }

        if(compteDeb.getSolde() >= paiement.getAmount().doubleValue()) {
            Payment payment2Save = new Payment(
                    UUID.randomUUID().toString(),
                    optionalCart.get(),
                    new Timestamp(System.currentTimeMillis()),
                    paiement.getAmount(),
                    paiement.getCountry(),
                    compteCred
            );


        Payment saved = paymentRepository.save(payment2Save);
        compteDeb.debiterCompte(paiement.getAmount().doubleValue());
        compteCred.crediterCompte(paiement.getAmount().doubleValue(), 1);

            if(cart.isVirtual()){
                cartRepository.delete(cart);
            }
            URI location = linkTo(methodOn(PaymentController.class).getOnePaiementById(userId, accountIban, carteId, saved.getId())).toUri();

            return ResponseEntity.created(location).build();
        }else {
            return ResponseEntity.badRequest().build();
        }
    }
}