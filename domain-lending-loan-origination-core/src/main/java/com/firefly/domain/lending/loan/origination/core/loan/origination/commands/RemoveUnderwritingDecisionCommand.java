package com.firefly.domain.lending.loan.origination.core.loan.origination.commands;

import com.firefly.common.cqrs.command.Command;

import java.util.UUID;

public record RemoveUnderwritingDecisionCommand(
        UUID loanApplicationId,
        UUID underwritingDecisionId
) implements Command<Void>{}