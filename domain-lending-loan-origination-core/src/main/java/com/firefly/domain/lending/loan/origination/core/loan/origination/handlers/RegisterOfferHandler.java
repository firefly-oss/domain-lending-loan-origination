package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import com.firefly.core.lending.origination.sdk.api.ProposedOfferApi;
import com.firefly.domain.lending.loan.origination.core.loan.origination.commands.RegisterProposedOfferCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class RegisterOfferHandler extends CommandHandler<RegisterProposedOfferCommand, UUID> {

    private final ProposedOfferApi proposedOfferApi;

    public RegisterOfferHandler(ProposedOfferApi proposedOfferApi) {
        this.proposedOfferApi = proposedOfferApi;
    }

    @Override
    protected Mono<UUID> doHandle(RegisterProposedOfferCommand cmd) {
        return proposedOfferApi.createOffer(cmd.getLoanApplicationId(), cmd, UUID.randomUUID().toString())
                .mapNotNull(proposedOfferDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(proposedOfferDTO)).getProposedOfferId());
    }
}