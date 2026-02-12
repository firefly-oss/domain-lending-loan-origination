package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import com.firefly.core.lending.origination.sdk.api.ProposedOfferApi;
import com.firefly.domain.lending.loan.origination.core.loan.origination.commands.RemoveProposedOfferCommand;
import reactor.core.publisher.Mono;

@CommandHandlerComponent
public class RemoveOfferHandler extends CommandHandler<RemoveProposedOfferCommand, Void> {

    private final ProposedOfferApi proposedOfferApi;

    public RemoveOfferHandler(ProposedOfferApi proposedOfferApi) {
        this.proposedOfferApi = proposedOfferApi;
    }

    @Override
    protected Mono<Void> doHandle(RemoveProposedOfferCommand cmd) {
        return proposedOfferApi.deleteOffer(cmd.loanApplicationId(), cmd.proposedOfferId(), null).then();
    }
}