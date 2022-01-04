package fr.miage.bank.entity;

import com.sun.istack.NotNull;
import javax.validation.constraints.Size;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AccountInput {
    @Size(min = 3)
    private String IBAN;
/*
    @NotNull
    private String lastname;

    @Size(min = 2)
    private String firstname;

    @NotNull
    private Date birthdate;



    @Size(min = 5)
    private String noPasseport;

    @Size(min = 10, max = 10)
    private String noTel;
*/

    @NotNull
    private String country;

    @Size(min = 5, max = 10)
    private String secret;

    private double solde;
    private String UserId;

}