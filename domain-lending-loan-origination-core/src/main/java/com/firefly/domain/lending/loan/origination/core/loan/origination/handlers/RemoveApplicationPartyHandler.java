package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.common.cqrs.annotations.CommandHandlerComponent;
import com.firefly.common.cqrs.command.CommandHandler;
import com.firefly.core.lending.origination.sdk.api.ApplicationPartyApi;
import com.firefly.domain.lending.loan.origination.core.loan.origination.commands.RemoveApplicationPartyCommand;
import reactor.core.publisher.Mono;

@CommandHandlerComponent
public class RemoveApplicationPartyHandler extends CommandHandler<RemoveApplicationPartyCommand, Void> {

    private final ApplicationPartyApi applicationPartyApi;

    public RemoveApplicationPartyHandler(ApplicationPartyApi applicationPartyApi) {
        this.applicationPartyApi = applicationPartyApi;
    }

    @Override
    protected Mono<Void> doHandle(RemoveApplicationPartyCommand cmd) {
        return applicationPartyApi.deleteParty(cmd.loanApplicationId(), cmd.applicationPartyId()).then();
    }
}
