package fr.miage.bank.input;

import fr.miage.bank.entity.Cart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentInput {

    @NotNull
    @Min(0)
    private BigDecimal amount;

    @NotNull
    private String country;

    @NotNull
    private String ibanCreditor;

    @NotNull
    private Cart cart;

    private Timestamp date;
}