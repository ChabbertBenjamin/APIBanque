package fr.miage.bank.validator;

import fr.miage.bank.input.PaymentInput;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

@Service
public class PaymentValidator {

    private Validator validator;

    public PaymentValidator(Validator validator) {
        this.validator = validator;
    }

    public void validate(PaymentInput paiement){
        Set<ConstraintViolation<PaymentInput>> violations = validator.validate(paiement);

        if(!violations.isEmpty()){
            throw new ConstraintViolationException(violations);
        }
    }
}