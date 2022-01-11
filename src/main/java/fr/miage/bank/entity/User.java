package fr.miage.bank.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private String id;

    private String lastname;
    private String firstname;
    private Date birthdate;
    private String country;

    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Role> roles = new ArrayList<>();

    private String noPassport;
    private String noTel;
    private String email;
    private String password;

    public User(String id, String lastname, String firstname, Date birthdate,String country, String noPassport, String noTel, String email, String password) {
        this.id = id;
        this.lastname = lastname;
        this.firstname = firstname;
        this.birthdate = birthdate;
        this.country = country;
        this.noPassport = noPassport;
        this.noTel = noTel;
        this.email = email;
        this.password = password;
    }
}