package fr.miage.bank.repository;

import fr.miage.bank.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository <Account, String> {
    public Iterable<Account> findAllByOwner_Id(String userId);

    public Optional<Account> findByOwner_IdAndIBAN(String userId, String iban);
}

