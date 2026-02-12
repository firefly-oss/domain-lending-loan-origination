package com.firefly.domain.lending.loan.origination.core.loan.origination.commands;

import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

public record RemoveLoanApplicationStatusHistoryCommand(
        UUID loanApplicationId,
        UUID statusHistoryId
) implements Command<Void>{}