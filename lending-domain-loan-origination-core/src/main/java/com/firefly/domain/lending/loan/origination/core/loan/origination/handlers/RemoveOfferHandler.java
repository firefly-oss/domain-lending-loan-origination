package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.common.cqrs.annotations.CommandHandlerComponent;
import com.firefly.common.cqrs.command.CommandHandler;
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
        return proposedOfferApi.deleteOffer(cmd.loanApplicationId(), cmd.proposedOfferId()).then();
    }
}