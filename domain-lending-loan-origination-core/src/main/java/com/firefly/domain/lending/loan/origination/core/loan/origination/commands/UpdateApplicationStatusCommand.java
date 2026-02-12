package com.firefly.domain.lending.loan.origination.core.loan.origination.commands;

import org.fireflyframework.cqrs.command.Command;
import com.firefly.core.lending.origination.sdk.model.LoanApplicationDTO;
import com.firefly.domain.lending.loan.origination.core.loan.origination.queries.GetApplicationStatusQuery;
import com.firefly.domain.lending.loan.origination.core.loan.origination.queries.GetLoanApplicationQuery;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UpdateApplicationStatusCommand implements Command<UUID> {
    private UUID loanApplicationId;
    private UUID applicationStatusId;
    private GetLoanApplicationQuery applicationQuery;
    private GetApplicationStatusQuery applicationStatusQuery;
    private LoanApplicationDTO loanApplication;
    private RegisterLoanApplicationStatusHistoryCommand statusHistoryCommand;

    public UpdateApplicationStatusCommand withApplicationStatusId(UUID applicationStatusId) {
        this.applicationStatusId = applicationStatusId;
        return this;
    }

    public UpdateApplicationStatusCommand withLoanApplication(LoanApplicationDTO loanApplication) {
        this.loanApplication = loanApplication;
        return this;
    }
}