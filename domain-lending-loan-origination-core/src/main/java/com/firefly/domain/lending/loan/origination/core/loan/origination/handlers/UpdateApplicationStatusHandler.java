package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.common.cqrs.command.CommandHandler;
import com.firefly.core.lending.origination.sdk.api.LoanApplicationsApi;
import com.firefly.core.lending.origination.sdk.model.LoanApplicationDTO;
import com.firefly.domain.lending.loan.origination.core.loan.origination.commands.UpdateApplicationStatusCommand;
import reactor.core.publisher.Mono;
import com.firefly.common.cqrs.annotations.CommandHandlerComponent;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class UpdateApplicationStatusHandler extends CommandHandler<UpdateApplicationStatusCommand, UUID> {

    private final LoanApplicationsApi loanApplicationsApi;

    public UpdateApplicationStatusHandler(LoanApplicationsApi loanApplicationsApi) {
        this.loanApplicationsApi = loanApplicationsApi;
    }

    @Override
    protected Mono<UUID> doHandle(UpdateApplicationStatusCommand cmd) {
        LoanApplicationDTO loanApplication = cmd.getLoanApplication();
        loanApplication.setApplicationStatusId(cmd.getApplicationStatusId());
        return loanApplicationsApi.updateLoanApplication(cmd.getLoanApplicationId(), cmd.getLoanApplication(), UUID.randomUUID().toString())
                .mapNotNull(productDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(productDTO).getLoanApplicationId()));
    }
}