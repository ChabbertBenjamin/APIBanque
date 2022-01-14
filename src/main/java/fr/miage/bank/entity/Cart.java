package fr.miage.bank.entity;

import lombok.*;
import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private String id;
    private String code;
    private String crypto;
    private boolean freeze;
    private boolean localisation;
    private double plafond;
    private boolean contactLess;
    private boolean virtual;
    private Date dateExpiry;
    private String num;

    @ManyToOne
    @JoinColumn(name = "account_IBAN")
    private Account account;
}
