package fr.miage.bank.entity;

import com.sun.istack.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountInput {

    @NotNull
    private String lastname;

    @Size(min = 2)
    private String firstname;

    @NotNull
    private Date birthdate;

    @NotNull
    private String country;

    @Size(min = 5)
    private String noPasseport;

    @Size(min = 10, max = 10)
    private String noTel;

    @Size(min = 5, max = 10)
    private String secret;

    @Size(min = 3)
    private String IBAN;
}