package fr.miage.bank.entity;

import lombok.*;
import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Account implements Serializable {


    @Id
    private String IBAN;

    private String country;
    private String secret;
    private double solde;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;


    public void debiterCompte(double amount){
        this.solde -= amount;
    }

    public void crediterCompte(double amount, double taux){
        this.solde += (amount*taux);
    }
}