package fr.miage.bank.entity;

import lombok.*;
import org.springframework.hateoas.PagedModel;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Account implements Serializable {


    @Id
    private String IBAN;
/*
    private String lastname;
    private String firstname;
    private Date birthdate;
    private String country;

    private String noPasseport;
    private String noTel;*/
    private String secret;
    private double solde;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;

}