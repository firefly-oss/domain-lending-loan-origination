package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.common.cqrs.annotations.CommandHandlerComponent;
import com.firefly.common.cqrs.command.CommandHandler;
import com.firefly.core.lending.origination.sdk.api.ApplicationPartyApi;
import com.firefly.domain.lending.loan.origination.core.loan.origination.commands.RegisterApplicationPartyCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class RegisterApplicationPartyHandler extends CommandHandler<RegisterApplicationPartyCommand, UUID> {

    private final ApplicationPartyApi applicationPartyApi;

    public RegisterApplicationPartyHandler(ApplicationPartyApi applicationPartyApi) {
        this.applicationPartyApi = applicationPartyApi;
    }

    @Override
    protected Mono<UUID> doHandle(RegisterApplicationPartyCommand cmd) {
        return applicationPartyApi.createParty(cmd.getLoanApplicationId(), cmd, UUID.randomUUID().toString())
                .mapNotNull(naturalPersonDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(naturalPersonDTO)).getApplicationPartyId());
    }
}