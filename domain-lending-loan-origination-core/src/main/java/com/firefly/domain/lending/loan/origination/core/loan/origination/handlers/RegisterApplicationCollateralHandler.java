package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.common.cqrs.annotations.CommandHandlerComponent;
import com.firefly.common.cqrs.command.CommandHandler;
import com.firefly.core.lending.origination.sdk.api.ApplicationCollateralApi;
import com.firefly.domain.lending.loan.origination.core.loan.origination.commands.RegisterApplicationCollateralCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class RegisterApplicationCollateralHandler extends CommandHandler<RegisterApplicationCollateralCommand, UUID> {

    private final ApplicationCollateralApi applicationCollateralApi;

    public RegisterApplicationCollateralHandler(ApplicationCollateralApi applicationCollateralApi) {
        this.applicationCollateralApi = applicationCollateralApi;
    }

    @Override
    protected Mono<UUID> doHandle(RegisterApplicationCollateralCommand cmd) {
        return applicationCollateralApi.createCollateral(cmd.getLoanApplicationId(), cmd, UUID.randomUUID().toString())
                .mapNotNull(applicationCollateralDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(applicationCollateralDTO)).getApplicationCollateralId());
    }
}