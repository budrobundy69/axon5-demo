package com.example.demo.account.query;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "accounts")
public class AccountEntity {

    @Id
    private String accountId;

    private long balance;

    protected AccountEntity() {
    }

    public AccountEntity(String accountId, long balance) {
        this.accountId = accountId;
        this.balance = balance;
    }

    public String getAccountId() {
        return accountId;
    }

    public long getBalance() {
        return balance;
    }

    public void deposit(long amount) {
        this.balance += amount;
    }
}
