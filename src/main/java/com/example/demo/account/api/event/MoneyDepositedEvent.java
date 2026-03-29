package com.example.demo.account.api.event;

public record MoneyDepositedEvent(String accountId, long amount) {
}
