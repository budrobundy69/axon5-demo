package com.example.demo.account.api.event;

public record AccountOpenedEvent(String accountId, long initialBalance) {
}
