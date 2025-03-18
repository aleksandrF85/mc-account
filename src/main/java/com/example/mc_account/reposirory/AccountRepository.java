package com.example.mc_account.reposirory;

import com.example.mc_account.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {

    Boolean existsByEmail(String email);

    Page<Account> findAllById (List<Long> ids, Pageable pageable);


}
