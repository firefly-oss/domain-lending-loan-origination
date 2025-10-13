package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.common.cqrs.annotations.CommandHandlerComponent;
import com.firefly.common.cqrs.command.CommandHandler;
import com.firefly.core.lending.origination.sdk.api.ApplicationDocumentApi;
import com.firefly.domain.lending.loan.origination.core.loan.origination.commands.RegisterApplicationDocumentCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class RegisterApplicationDocumentHandler extends CommandHandler<RegisterApplicationDocumentCommand, UUID> {

    private final ApplicationDocumentApi applicationDocumentApi;

    public RegisterApplicationDocumentHandler(ApplicationDocumentApi applicationDocumentApi) {
        this.applicationDocumentApi = applicationDocumentApi;
    }

    @Override
    protected Mono<UUID> doHandle(RegisterApplicationDocumentCommand cmd) {
        return applicationDocumentApi.createDocument(cmd.getLoanApplicationId(), cmd, UUID.randomUUID().toString())
                .mapNotNull(applicationDocumentDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(applicationDocumentDTO)).getApplicationDocumentId());
    }
}