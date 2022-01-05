package fr.miage.bank.validator;

import fr.miage.bank.input.CartInput;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

@Service
public class CartValidator {

    private final Validator validator;

    public CartValidator(Validator validator) {
        this.validator = validator;
    }

    public void validate(CartInput cart){
        Set<ConstraintViolation<CartInput>> violations = validator.validate(cart);

        if(!violations.isEmpty()){
            throw new ConstraintViolationException(violations);
        }
    }
}
