package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import com.firefly.core.lending.origination.sdk.api.LoanApplicationStatusHistoryApi;
import com.firefly.domain.lending.loan.origination.core.loan.origination.commands.RemoveLoanApplicationStatusHistoryCommand;
import reactor.core.publisher.Mono;

@CommandHandlerComponent
public class RemoveStatusHandler extends CommandHandler<RemoveLoanApplicationStatusHistoryCommand, Void> {

    private final LoanApplicationStatusHistoryApi loanApplicationStatusHistoryApi;

    public RemoveStatusHandler(LoanApplicationStatusHistoryApi loanApplicationStatusHistoryApi) {
        this.loanApplicationStatusHistoryApi = loanApplicationStatusHistoryApi;
    }

    @Override
    protected Mono<Void> doHandle(RemoveLoanApplicationStatusHistoryCommand cmd) {
        return loanApplicationStatusHistoryApi.deleteStatusHistory(cmd.loanApplicationId(), cmd.statusHistoryId(), null).then();
    }
}