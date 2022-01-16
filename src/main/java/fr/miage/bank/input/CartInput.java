package fr.miage.bank.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartInput {

    @NotNull
    @Pattern(regexp = "([0-9]{4})")
    private String code;

    @NotNull
    @Pattern(regexp = "([0-9]{3})")
    private String crypto;

    @NotNull
    private boolean freeze;

    @NotNull
    private boolean localisation;

    @NotNull
    @Min(0)
    private double plafond;

    @NotNull
    private boolean contactLess;

    @NotNull
    private boolean virtual;

    @NotNull
    private Date dateExpiry;

    @NotNull
    @Pattern(regexp = "([0-9]{16})")
    private String num;
}
