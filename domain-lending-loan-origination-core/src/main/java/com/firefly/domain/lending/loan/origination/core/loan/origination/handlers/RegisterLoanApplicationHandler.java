package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.common.cqrs.annotations.CommandHandlerComponent;
import com.firefly.common.cqrs.command.CommandHandler;
import com.firefly.core.lending.origination.sdk.api.LoanApplicationsApi;
import com.firefly.domain.lending.loan.origination.core.loan.origination.commands.RegisterLoanApplicationCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class RegisterLoanApplicationHandler extends CommandHandler<RegisterLoanApplicationCommand, UUID> {

    private final LoanApplicationsApi loanApplicationsApi;

    public RegisterLoanApplicationHandler(LoanApplicationsApi loanApplicationsApi) {
        this.loanApplicationsApi = loanApplicationsApi;
    }

    @Override
    protected Mono<UUID> doHandle(RegisterLoanApplicationCommand cmd) {
        return loanApplicationsApi.createLoanApplication(cmd, UUID.randomUUID().toString())
                .mapNotNull(naturalPersonDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(naturalPersonDTO)).getLoanApplicationId());
    }
}