package fr.miage.bank.repository;

import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository <Account, String> {
    Iterable<Account> findAllByOwner_Id(String userId);
    //Optional<Account> findByOwner_IdAndIBAN(String userId, String iban);
    Optional<Account> findByOwnerIdAndIBAN(String userId, String iban);
    Optional<Account> findByOwnerAndIBAN(Optional<User> user, String iban);
}

