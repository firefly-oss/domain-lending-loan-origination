package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.common.cqrs.annotations.CommandHandlerComponent;
import com.firefly.common.cqrs.command.CommandHandler;
import com.firefly.core.lending.origination.sdk.api.UnderwritingScoreApi;
import com.firefly.domain.lending.loan.origination.core.loan.origination.commands.RegisterUnderwritingScoreCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class RegisterScoreHandler extends CommandHandler<RegisterUnderwritingScoreCommand, UUID> {

    private final UnderwritingScoreApi underwritingScoreApi;

    public RegisterScoreHandler(UnderwritingScoreApi underwritingScoreApi) {
        this.underwritingScoreApi = underwritingScoreApi;
    }

    @Override
    protected Mono<UUID> doHandle(RegisterUnderwritingScoreCommand cmd) {
        return underwritingScoreApi.createScore(cmd.getLoanApplicationId(), cmd, UUID.randomUUID().toString())
                .mapNotNull(underwritingScoreDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(underwritingScoreDTO)).getUnderwritingScoreId());
    }
}