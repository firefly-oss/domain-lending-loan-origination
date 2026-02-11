package com.firefly.domain.lending.loan.origination.core.loan.origination.workflows;

import com.firefly.common.cqrs.command.CommandBus;
import com.firefly.common.cqrs.query.QueryBus;
import com.firefly.core.lending.origination.sdk.model.ApplicationStatusDTO;
import com.firefly.core.lending.origination.sdk.model.LoanApplicationDTO;
import com.firefly.core.lending.origination.sdk.model.LoanApplicationStatusHistoryDTO;
import com.firefly.domain.lending.loan.origination.core.loan.origination.queries.GetApplicationStatusQuery;
import com.firefly.domain.lending.loan.origination.core.loan.origination.queries.GetLoanApplicationQuery;
import com.firefly.domain.lending.loan.origination.core.loan.origination.commands.RegisterLoanApplicationStatusHistoryCommand;
import com.firefly.domain.lending.loan.origination.core.loan.origination.commands.UpdateApplicationStatusCommand;
import com.firefly.transactional.saga.annotations.Saga;
import com.firefly.transactional.saga.annotations.SagaStep;
import com.firefly.transactional.saga.annotations.StepEvent;
import com.firefly.transactional.saga.core.SagaContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.firefly.domain.lending.loan.origination.core.loan.utils.constants.GlobalConstants.*;
import static com.firefly.domain.lending.loan.origination.core.loan.utils.constants.RegisterApplicationConstants.*;

@Saga(name = SAGA_UPDATE_APPLICATION_STATUS)
@Service
public class UpdateApplicationStatusSaga {

    private final CommandBus commandBus;
    private final QueryBus queryBus;

    @Autowired
    public UpdateApplicationStatusSaga(CommandBus commandBus, QueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }

    @SagaStep(id = STEP_RETRIEVE_APPLICATION_STATUS)
    @StepEvent(type = EVENT_APPLICATION_STATUS_RETRIEVED)
    public Mono<ApplicationStatusDTO> retrieveApplicationStatus(GetApplicationStatusQuery cmd, SagaContext ctx) {
        return queryBus.query(cmd)
                .doOnNext(applicationStatus -> ctx.variables().put(CTX_APPLICATION_STATUS, applicationStatus));
    }

    @SagaStep(id = STEP_RETRIEVE_APPLICATION)
    @StepEvent(type = EVENT_APPLICATION_RETRIEVED)
    public Mono<LoanApplicationDTO> retrieveLoanApplication(GetLoanApplicationQuery cmd, SagaContext ctx) {
        return queryBus.query(cmd)
                .doOnNext(loanApplicationDTO -> ctx.variables().put(CTX_LOAN_APPLICATION, loanApplicationDTO));
    }

    @SagaStep(id = STEP_RETRIEVE_OLD_APPLICATION_STATUS, dependsOn = STEP_RETRIEVE_APPLICATION)
    @StepEvent(type = EVENT_OLD_APPLICATION_STATUS_RETRIEVED)
    public Mono<ApplicationStatusDTO> retrieveOldApplicationStatus(GetApplicationStatusQuery cmd, SagaContext ctx) {
        return queryBus.query(cmd.withApplicationStatusId(ctx.getVariableAs(CTX_LOAN_APPLICATION, LoanApplicationDTO.class).getApplicationStatusId()))
                .doOnNext(oldApplicationStatus -> ctx.variables().put(CTX_OLD_APPLICATION_STATUS, oldApplicationStatus));
    }

    @SagaStep(id = STEP_UPDATE_APPLICATION_STATUS, dependsOn = STEP_RETRIEVE_APPLICATION_STATUS)
    @StepEvent(type = EVENT_APPLICATION_STATUS_UPDATED)
    public Mono<UUID> updateApplicationStatus(UpdateApplicationStatusCommand cmd, SagaContext ctx) {
        return commandBus.send(cmd
                        .withLoanApplication(ctx.getVariableAs(CTX_LOAN_APPLICATION, LoanApplicationDTO.class))
                        .withApplicationStatusId(ctx.getVariableAs(CTX_APPLICATION_STATUS, ApplicationStatusDTO.class).getApplicationStatusId()));
    }

    @SagaStep(id = STEP_UPDATE_APPLICATION_STATUS_HISTORY, dependsOn = STEP_UPDATE_APPLICATION_STATUS)
    @StepEvent(type = EVENT_APPLICATION_STATUS_HISTORY_UPDATED)
    public Mono<UUID> updateApplicationStatusHistory(UpdateApplicationStatusCommand cmd, SagaContext ctx) {
        RegisterLoanApplicationStatusHistoryCommand cmdHistory = cmd.getStatusHistoryCommand();
        cmdHistory.setLoanApplicationId(ctx.getVariableAs(CTX_LOAN_APPLICATION, LoanApplicationDTO.class).getLoanApplicationId());
        cmdHistory.setOldStatus(LoanApplicationStatusHistoryDTO.OldStatusEnum.valueOf(ctx.getVariableAs(CTX_OLD_APPLICATION_STATUS, ApplicationStatusDTO.class).getCode()));
        cmdHistory.setNewStatus(LoanApplicationStatusHistoryDTO.NewStatusEnum.valueOf(ctx.getVariableAs(CTX_APPLICATION_STATUS, ApplicationStatusDTO.class).getCode()));
        cmdHistory.setChangeReason("User request to update application status");
        cmdHistory.setChangedBy("system-auto-process");
        cmdHistory.setChangedAt(LocalDateTime.now());
        return commandBus.send(cmdHistory);
    }

}