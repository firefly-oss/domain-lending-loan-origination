package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import com.firefly.core.lending.origination.sdk.api.UnderwritingDecisionApi;
import com.firefly.domain.lending.loan.origination.core.loan.origination.commands.RemoveUnderwritingDecisionCommand;
import reactor.core.publisher.Mono;

@CommandHandlerComponent
public class RemoveDecisionHandler extends CommandHandler<RemoveUnderwritingDecisionCommand, Void> {

    private final UnderwritingDecisionApi underwritingDecisionApi;

    public RemoveDecisionHandler(UnderwritingDecisionApi underwritingDecisionApi) {
        this.underwritingDecisionApi = underwritingDecisionApi;
    }

    @Override
    protected Mono<Void> doHandle(RemoveUnderwritingDecisionCommand cmd) {
        return underwritingDecisionApi.deleteDecision(cmd.loanApplicationId(), cmd.underwritingDecisionId(), null).then();
    }
}