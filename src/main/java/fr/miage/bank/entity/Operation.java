package fr.miage.bank.entity;

import lombok.*;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private String id;

    private Date date;
    private Date time;
    private String text;
    private double amount;
    private double taux;

    private String IBAN_creditor;
    private String nameCreditor;

    @OneToOne
    private Account compteOwner;


    private String category;
    private String country;

}
