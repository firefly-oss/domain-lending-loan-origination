package io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import io.fireflysoftware.core.lending.origination.sdk.api.LoanApplicationsApi;
import io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.commands.RemoveApplicationPartyCommand;
import io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.commands.RemoveLoanApplicationCommand;
import reactor.core.publisher.Mono;
import java.util.UUID;

@CommandHandlerComponent
public class RemoveLoanApplicationHandler extends CommandHandler<RemoveLoanApplicationCommand, Void> {

    private final LoanApplicationsApi loanApplicationsApi;

    public RemoveLoanApplicationHandler(LoanApplicationsApi loanApplicationsApi) {
        this.loanApplicationsApi = loanApplicationsApi;
    }

    @Override
    protected Mono<Void> doHandle(RemoveLoanApplicationCommand cmd) {
        return loanApplicationsApi.deleteLoanApplication(cmd.loanApplicationId(), UUID.randomUUID().toString()).then();
    }
}
