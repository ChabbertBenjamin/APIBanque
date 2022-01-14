package fr.miage.bank.input;

import com.sun.istack.NotNull;
import javax.validation.constraints.Size;

import fr.miage.bank.entity.User;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AccountInput {
    @NotNull
    private String country;

    @Size(min = 5, max = 10)
    private String secret;
    @NotNull
    private double solde;
    @NotNull
    private User user;

}