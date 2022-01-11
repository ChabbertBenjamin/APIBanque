package fr.miage.bank.entity;

import lombok.*;
import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Optional;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Operation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private String id;

    private Timestamp date;
    private String text;
    private double amount;
    private double taux;

    @ManyToOne
    @JoinColumn(name = "creditor_account_iban")
    private Account compteCreditor;

    @ManyToOne
    @JoinColumn(name = "debitor_account_iban")
    private Account compteDebitor;

    private String nameCreditor;

    private String category;
    private String country;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

}
