package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import com.firefly.core.lending.origination.sdk.api.UnderwritingScoreApi;
import com.firefly.domain.lending.loan.origination.core.loan.origination.commands.RemoveUnderwritingScoreCommand;
import reactor.core.publisher.Mono;

@CommandHandlerComponent
public class RemoveScoreHandler extends CommandHandler<RemoveUnderwritingScoreCommand, Void> {

    private final UnderwritingScoreApi underwritingScoreApi;

    public RemoveScoreHandler(UnderwritingScoreApi underwritingScoreApi) {
        this.underwritingScoreApi = underwritingScoreApi;
    }

    @Override
    protected Mono<Void> doHandle(RemoveUnderwritingScoreCommand cmd) {
        return underwritingScoreApi.deleteScore(cmd.loanApplicationId(), cmd.underwritingScoreId(), null).then();
    }
}