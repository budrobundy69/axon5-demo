package com.example.demo.account.command;

import com.example.demo.account.api.command.DepositMoneyCommand;
import com.example.demo.account.api.command.OpenAccountCommand;
import com.example.demo.account.api.event.AccountOpenedEvent;
import com.example.demo.account.api.event.MoneyDepositedEvent;
import org.axonframework.messaging.commandhandling.annotation.CommandHandler;
import org.axonframework.messaging.eventhandling.gateway.EventGateway;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AccountCommandHandler {

    private final EventGateway eventGateway;
    private final Map<String, Long> balances = new ConcurrentHashMap<>();

    public AccountCommandHandler(EventGateway eventGateway) {
        this.eventGateway = eventGateway;
    }

    @CommandHandler
    public void handle(OpenAccountCommand command) {
        Long previous = balances.putIfAbsent(command.accountId(), command.initialBalance());
        if (previous != null) {
            throw new IllegalArgumentException("Account already exists: " + command.accountId());
        }
        eventGateway.publish(List.of(new AccountOpenedEvent(command.accountId(), command.initialBalance()))).join();
    }

    @CommandHandler
    public void handle(DepositMoneyCommand command) {
        Long currentBalance = balances.get(command.accountId());
        if (currentBalance == null) {
            throw new IllegalArgumentException("Account not found: " + command.accountId());
        }
        balances.computeIfPresent(command.accountId(), (id, value) -> value + command.amount());
        eventGateway.publish(List.of(new MoneyDepositedEvent(command.accountId(), command.amount()))).join();
    }
}
