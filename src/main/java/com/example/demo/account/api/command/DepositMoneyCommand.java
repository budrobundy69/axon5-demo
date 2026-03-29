package com.example.demo.account.api.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record DepositMoneyCommand(
        @NotBlank String accountId,
        @Positive long amount
) {
}
