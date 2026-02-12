package com.firefly.domain.lending.loan.origination.core.loan.origination.commands;

import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

public record RemoveUnderwritingDecisionCommand(
        UUID loanApplicationId,
        UUID underwritingDecisionId
) implements Command<Void>{}