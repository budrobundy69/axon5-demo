package com.example.demo.account.command;

import com.example.demo.account.api.command.DepositMoneyCommand;
import com.example.demo.account.api.command.OpenAccountCommand;
import org.axonframework.messaging.eventhandling.gateway.EventGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountCommandHandlerTest {

    @Mock
    private EventGateway eventGateway;

    private AccountCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new AccountCommandHandler(eventGateway);
    }

    @Test
    void openAndDepositPublishesEvents() {
        when(eventGateway.publish(anyList())).thenReturn(CompletableFuture.completedFuture(null));

        handler.handle(new OpenAccountCommand("acc-1", 100));
        handler.handle(new DepositMoneyCommand("acc-1", 25));

        verify(eventGateway, times(2)).publish(anyList());
    }

    @Test
    void openingSameAccountTwiceThrows() {
        when(eventGateway.publish(anyList())).thenReturn(CompletableFuture.completedFuture(null));

        handler.handle(new OpenAccountCommand("acc-1", 100));

        assertThrows(IllegalArgumentException.class, () -> handler.handle(new OpenAccountCommand("acc-1", 100)));
        verify(eventGateway, times(1)).publish(anyList());
    }

    @Test
    void depositingUnknownAccountThrows() {
        assertThrows(IllegalArgumentException.class, () -> handler.handle(new DepositMoneyCommand("missing", 25)));
        verify(eventGateway, never()).publish(anyList());
    }
}
