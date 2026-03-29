package com.example.demo.account.query;

import com.example.demo.account.api.event.AccountOpenedEvent;
import com.example.demo.account.api.event.MoneyDepositedEvent;
import com.example.demo.account.api.query.FindAccountQuery;
import com.example.demo.account.api.query.FindAllAccountsQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class AccountProjectionTest {

    @Autowired
    private AccountProjection projection;

    @Autowired
    private AccountViewRepository accountViewRepository;

    @BeforeEach
    void setUp() {
        accountViewRepository.deleteAll();
    }

    @Test
    void updatesReadModelFromEvents() {
        projection.on(new AccountOpenedEvent("acc-1", 100));
        projection.on(new MoneyDepositedEvent("acc-1", 40));

        AccountView account = projection.handle(new FindAccountQuery("acc-1"));
        assertNotNull(account);
        assertEquals("acc-1", account.accountId());
        assertEquals(140, account.balance());
    }

    @Test
    void returnsAllAccounts() {
        projection.on(new AccountOpenedEvent("acc-1", 100));
        projection.on(new AccountOpenedEvent("acc-2", 200));

        List<AccountView> accounts = projection.handle(new FindAllAccountsQuery());
        assertEquals(2, accounts.size());
    }
}
