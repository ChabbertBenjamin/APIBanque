package fr.miage.bank.input;

import com.sun.istack.NotNull;
import javax.validation.constraints.Size;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AccountInput {
    @Size(min = 3)
    private String IBAN;

    @NotNull
    private String country;

    @Size(min = 5, max = 10)
    private String secret;

    private double solde;
    private String UserId;

}