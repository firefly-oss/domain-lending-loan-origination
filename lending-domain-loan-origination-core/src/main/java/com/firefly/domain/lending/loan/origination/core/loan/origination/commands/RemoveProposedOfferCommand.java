package com.firefly.domain.lending.loan.origination.core.loan.origination.commands;

import com.firefly.common.cqrs.command.Command;

import java.util.UUID;

public record RemoveProposedOfferCommand(
        UUID loanApplicationId,
        UUID proposedOfferId
) implements Command<Void>{}