package fr.miage.bank.entity;

import lombok.*;
import javax.validation.constraints.Size;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInput {

    @Size(min = 3)
    private String lastname;

    @Size(min = 2)
    private String firstname;

    private String email;

    @Size(min = 4)
    private String password;
}