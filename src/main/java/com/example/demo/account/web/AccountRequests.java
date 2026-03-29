package com.example.demo.account.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class AccountRequests {

    public record OpenAccountRequest(
            @NotBlank String accountId,
            @PositiveOrZero long initialBalance
    ) {
    }

    public record DepositRequest(
            @Positive long amount
    ) {
    }
}
