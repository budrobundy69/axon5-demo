package com.example.demo.account.command;

import com.example.demo.account.api.command.DepositMoneyCommand;
import com.example.demo.account.api.command.OpenAccountCommand;
import com.example.demo.account.api.event.AccountOpenedEvent;
import com.example.demo.account.api.event.MoneyDepositedEvent;
import org.axonframework.messaging.commandhandling.configuration.CommandHandlingModule;
import org.axonframework.messaging.core.configuration.MessagingConfigurer;
import org.axonframework.messaging.eventhandling.gateway.EventGateway;
import org.axonframework.test.fixture.AxonTestFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class AccountCommandHandlerTest {

    private AxonTestFixture fixture;

    @BeforeEach
    void setUp() {
        fixture = AxonTestFixture.with(
                MessagingConfigurer.create()
                        .registerCommandHandlingModule(() ->
                                CommandHandlingModule.named("account-command-handlers")
                                        .commandHandlers()
                                        .autodetectedCommandHandlingComponent(
                                                config -> new AccountCommandHandler(config.getComponent(EventGateway.class))
                                        )
                                        .build()
                        ),
                AxonTestFixture.Customization::disableAxonServer
        );
    }

    @AfterEach
    void tearDown() {
        fixture.stop();
    }

    @Test
    void givenNoAccountWhenOpenAccountThenPublishesAccountOpenedEvent() {
        fixture.given()
               .noPriorActivity()
               .when()
               .command(new OpenAccountCommand("acc-1", 100))
               .then()
               .success()
               .events(new AccountOpenedEvent("acc-1", 100));
    }

    @Test
    void givenExistingAccountWhenOpenAccountAgainThenThrowsException() {
        fixture.given()
               .command(new OpenAccountCommand("acc-1", 100))
               .when()
               .command(new OpenAccountCommand("acc-1", 100))
               .then()
               .exceptionSatisfies(throwable -> {
                   Throwable root = throwable.getCause() != null ? throwable.getCause() : throwable;
                   assertInstanceOf(IllegalArgumentException.class, root);
                   assertEquals("Account already exists: acc-1", root.getMessage());
               })
               .noEvents();
    }

    @Test
    void givenExistingAccountWhenDepositMoneyThenPublishesMoneyDepositedEvent() {
        fixture.given()
               .command(new OpenAccountCommand("acc-1", 100))
               .when()
               .command(new DepositMoneyCommand("acc-1", 25))
               .then()
               .success()
               .events(new MoneyDepositedEvent("acc-1", 25));
    }

    @Test
    void givenMissingAccountWhenDepositMoneyThenThrowsException() {
        fixture.given()
               .noPriorActivity()
               .when()
               .command(new DepositMoneyCommand("missing", 25))
               .then()
               .exceptionSatisfies(throwable -> {
                   Throwable root = throwable.getCause() != null ? throwable.getCause() : throwable;
                   assertInstanceOf(IllegalArgumentException.class, root);
                   assertEquals("Account not found: missing", root.getMessage());
               })
               .noEvents();
    }
}
