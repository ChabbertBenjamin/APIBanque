package fr.miage.bank.input;

import lombok.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OperationInput {

    @NotNull
    private Timestamp date;

    @NotNull
    private String text;

    @NotNull
    private double amount;

    @NotNull
    private double taux;

    @NotNull
    private String nameCreditor;

    @NotNull
    private String category;

    @NotNull
    private String country;
}
