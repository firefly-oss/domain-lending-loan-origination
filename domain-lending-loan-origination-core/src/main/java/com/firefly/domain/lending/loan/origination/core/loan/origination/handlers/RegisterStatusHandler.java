package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.common.cqrs.annotations.CommandHandlerComponent;
import com.firefly.common.cqrs.command.CommandHandler;
import com.firefly.core.lending.origination.sdk.api.LoanApplicationStatusHistoryApi;
import com.firefly.domain.lending.loan.origination.core.loan.origination.commands.RegisterLoanApplicationStatusHistoryCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class RegisterStatusHandler extends CommandHandler<RegisterLoanApplicationStatusHistoryCommand, UUID> {

    private final LoanApplicationStatusHistoryApi loanApplicationStatusHistoryApi;

    public RegisterStatusHandler(LoanApplicationStatusHistoryApi loanApplicationStatusHistoryApi) {
        this.loanApplicationStatusHistoryApi = loanApplicationStatusHistoryApi;
    }

    @Override
    protected Mono<UUID> doHandle(RegisterLoanApplicationStatusHistoryCommand cmd) {
        return loanApplicationStatusHistoryApi.createStatusHistory(cmd.getLoanApplicationId(), cmd, UUID.randomUUID().toString())
                .mapNotNull(statusHistoryDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(statusHistoryDTO)).getStatusHistoryId());
    }
}