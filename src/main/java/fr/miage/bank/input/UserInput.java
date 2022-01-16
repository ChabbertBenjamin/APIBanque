package fr.miage.bank.input;

import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInput {

    @NotNull
    @Size(min = 3)
    private String lastname;

    @NotNull
    @Size(min = 2)
    private String firstname;

    @NotNull
    @Column(unique=true)
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
    private String email;

    @NotNull
    @Size(min = 4)
    private String password;

    @NotNull
    private Date birthdate;

    @NotNull
    private String country;

    @NotNull
    private String noPassport;

    @NotNull
    @Pattern(regexp = "([0-9]*)")
    private String noTel;
}