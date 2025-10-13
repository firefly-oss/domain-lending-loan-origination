package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.common.cqrs.annotations.CommandHandlerComponent;
import com.firefly.common.cqrs.command.CommandHandler;
import com.firefly.core.lending.origination.sdk.api.UnderwritingDecisionApi;
import com.firefly.domain.lending.loan.origination.core.loan.origination.commands.RegisterUnderwritingDecisionCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class RegisterDecisionHandler extends CommandHandler<RegisterUnderwritingDecisionCommand, UUID> {

    private final UnderwritingDecisionApi underwritingDecisionApi;

    public RegisterDecisionHandler(UnderwritingDecisionApi underwritingDecisionApi) {
        this.underwritingDecisionApi = underwritingDecisionApi;
    }

    @Override
    protected Mono<UUID> doHandle(RegisterUnderwritingDecisionCommand cmd) {
        return underwritingDecisionApi.createDecision(cmd.getLoanApplicationId(), cmd, UUID.randomUUID().toString())
                .mapNotNull(underwritingDecisionDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(underwritingDecisionDTO)).getUnderwritingDecisionId());
    }
}