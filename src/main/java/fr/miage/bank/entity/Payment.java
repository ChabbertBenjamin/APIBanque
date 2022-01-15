package fr.miage.bank.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    private Timestamp date;
    private BigDecimal amount;

    private String country;

    @ManyToOne
    @JoinColumn(name = "crebitor_account_iban")
    private Account compteCreditor;

}