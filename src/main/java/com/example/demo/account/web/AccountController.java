package com.example.demo.account.web;

import com.example.demo.account.api.command.DepositMoneyCommand;
import com.example.demo.account.api.command.OpenAccountCommand;
import com.example.demo.account.api.query.FindAccountQuery;
import com.example.demo.account.api.query.FindAllAccountsQuery;
import com.example.demo.account.query.AccountView;
import jakarta.validation.Valid;
import org.axonframework.messaging.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.queryhandling.gateway.QueryGateway;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public AccountController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void openAccount(@Valid @RequestBody AccountRequests.OpenAccountRequest request) {
        commandGateway.sendAndWait(new OpenAccountCommand(request.accountId(), request.initialBalance()));
    }

    @PostMapping("/{accountId}/deposits")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deposit(
            @PathVariable String accountId,
            @Valid @RequestBody AccountRequests.DepositRequest request
    ) {
        commandGateway.sendAndWait(new DepositMoneyCommand(accountId, request.amount()));
    }

    @GetMapping("/{accountId}")
    public AccountView getAccount(@PathVariable String accountId) {
        return queryGateway.query(new FindAccountQuery(accountId), AccountView.class).join();
    }

    @GetMapping
    public List<AccountView> getAllAccounts() {
        return queryGateway.queryMany(new FindAllAccountsQuery(), AccountView.class).join();
    }
}
