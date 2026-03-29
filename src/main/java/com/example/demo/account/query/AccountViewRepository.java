package com.example.demo.account.query;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountViewRepository extends JpaRepository<AccountEntity, String> {
}
