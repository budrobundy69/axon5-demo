package com.example.demo.account.api.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record OpenAccountCommand(
        @NotBlank String accountId,
        @PositiveOrZero long initialBalance
) {
}
