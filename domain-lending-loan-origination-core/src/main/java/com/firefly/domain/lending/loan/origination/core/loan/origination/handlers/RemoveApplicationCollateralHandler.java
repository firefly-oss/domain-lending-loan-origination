package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.common.cqrs.annotations.CommandHandlerComponent;
import com.firefly.common.cqrs.command.CommandHandler;
import com.firefly.core.lending.origination.sdk.api.ApplicationCollateralApi;
import com.firefly.domain.lending.loan.origination.core.loan.origination.commands.RemoveApplicationCollateralCommand;
import reactor.core.publisher.Mono;

@CommandHandlerComponent
public class RemoveApplicationCollateralHandler extends CommandHandler<RemoveApplicationCollateralCommand, Void> {

    private final ApplicationCollateralApi applicationCollateralApi;

    public RemoveApplicationCollateralHandler(ApplicationCollateralApi applicationCollateralApi) {
        this.applicationCollateralApi = applicationCollateralApi;
    }

    @Override
    protected Mono<Void> doHandle(RemoveApplicationCollateralCommand cmd) {
        return applicationCollateralApi.deleteCollateral(cmd.loanApplicationId(), cmd.applicationCollateralId()).then();
    }
}