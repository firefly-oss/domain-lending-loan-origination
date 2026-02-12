package com.firefly.domain.lending.loan.origination.core.loan.origination.commands;

import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

public record RemoveApplicationDocumentCommand(
        UUID loanApplicationId,
        UUID applicationDocumentId
) implements Command<Void>{}
