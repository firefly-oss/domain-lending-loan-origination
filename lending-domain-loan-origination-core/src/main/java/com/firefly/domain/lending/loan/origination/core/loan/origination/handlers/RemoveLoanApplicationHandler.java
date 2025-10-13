package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.common.cqrs.annotations.CommandHandlerComponent;
import com.firefly.common.cqrs.command.CommandHandler;
import com.firefly.core.lending.origination.sdk.api.LoanApplicationsApi;
import com.firefly.domain.lending.loan.origination.core.loan.origination.commands.RemoveApplicationPartyCommand;
import com.firefly.domain.lending.loan.origination.core.loan.origination.commands.RemoveLoanApplicationCommand;
import reactor.core.publisher.Mono;

@CommandHandlerComponent
public class RemoveLoanApplicationHandler extends CommandHandler<RemoveLoanApplicationCommand, Void> {

    private final LoanApplicationsApi loanApplicationsApi;

    public RemoveLoanApplicationHandler(LoanApplicationsApi loanApplicationsApi) {
        this.loanApplicationsApi = loanApplicationsApi;
    }

    @Override
    protected Mono<Void> doHandle(RemoveLoanApplicationCommand cmd) {
        return loanApplicationsApi.deleteLoanApplication(cmd.loanApplicationId()).then();
    }
}
