package com.example.demo.account.query;

import com.example.demo.account.api.event.AccountOpenedEvent;
import com.example.demo.account.api.event.MoneyDepositedEvent;
import com.example.demo.account.api.query.FindAccountQuery;
import com.example.demo.account.api.query.FindAllAccountsQuery;
import org.axonframework.messaging.eventhandling.annotation.EventHandler;
import org.axonframework.messaging.queryhandling.annotation.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccountProjection {

    private final AccountViewRepository accountViewRepository;

    public AccountProjection(AccountViewRepository accountViewRepository) {
        this.accountViewRepository = accountViewRepository;
    }

    @EventHandler
    public void on(AccountOpenedEvent event) {
        accountViewRepository.save(new AccountEntity(event.accountId(), event.initialBalance()));
    }

    @EventHandler
    public void on(MoneyDepositedEvent event) {
        accountViewRepository.findById(event.accountId())
                .ifPresent(account -> {
                    account.deposit(event.amount());
                    accountViewRepository.save(account);
                });
    }

    @QueryHandler
    public AccountView handle(FindAccountQuery query) {
        return accountViewRepository.findById(query.accountId())
                .map(this::toView)
                .orElse(null);
    }

    @QueryHandler
    public List<AccountView> handle(FindAllAccountsQuery query) {
        return accountViewRepository.findAll()
                .stream()
                .map(this::toView)
                .toList();
    }

    private AccountView toView(AccountEntity account) {
        return new AccountView(account.getAccountId(), account.getBalance());
    }
}
