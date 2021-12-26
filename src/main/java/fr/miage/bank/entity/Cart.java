package fr.miage.bank.entity;

import lombok.*;

import javax.persistence.*;

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

    private int code;
    private int crypto;
    private boolean freeze;
    private boolean localisation;
    private int plafond;
    private boolean contactLess;
    private boolean virtual;

    @ManyToOne
    private Account account;
}
