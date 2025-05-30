package com.example.mc_account.reposirory;

import com.example.mc_account.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID>, JpaSpecificationExecutor<Account> {

    Boolean existsByEmail(String email);

    Optional<Account> findByEmail(String email);

    @Query("SELECT COUNT(a) FROM Account a WHERE a.deleted = false")
    long countActiveAccounts();
}
