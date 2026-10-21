/*
 * Copyright 2025 Firefly Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.workflows;

import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.cqrs.query.QueryBus;
import io.fireflysoftware.core.lending.origination.sdk.model.ApplicationStatusDTO;
import io.fireflysoftware.core.lending.origination.sdk.model.LoanApplicationDTO;
import io.fireflysoftware.core.lending.origination.sdk.model.LoanApplicationStatusHistoryDTO;
import io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.queries.GetApplicationStatusQuery;
import io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.queries.GetLoanApplicationQuery;
import io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.commands.RegisterLoanApplicationStatusHistoryCommand;
import io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.commands.RemoveLoanApplicationStatusHistoryCommand;
import io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.commands.UpdateApplicationStatusCommand;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static io.fireflysoftware.domain.lending.loan.origination.core.loan.utils.constants.GlobalConstants.*;
import static io.fireflysoftware.domain.lending.loan.origination.core.loan.utils.constants.RegisterApplicationConstants.*;

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
    public Mono<ApplicationStatusDTO> retrieveApplicationStatus(GetApplicationStatusQuery cmd, ExecutionContext ctx) {
        return queryBus.query(cmd)
                .doOnNext(applicationStatus -> ctx.putVariable(CTX_APPLICATION_STATUS, applicationStatus));
    }

    @SagaStep(id = STEP_RETRIEVE_APPLICATION)
    @StepEvent(type = EVENT_APPLICATION_RETRIEVED)
    public Mono<LoanApplicationDTO> retrieveLoanApplication(GetLoanApplicationQuery cmd, ExecutionContext ctx) {
        return queryBus.query(cmd)
                .doOnNext(loanApplicationDTO -> ctx.putVariable(CTX_LOAN_APPLICATION, loanApplicationDTO));
    }

    @SagaStep(id = STEP_RETRIEVE_OLD_APPLICATION_STATUS, dependsOn = STEP_RETRIEVE_APPLICATION)
    @StepEvent(type = EVENT_OLD_APPLICATION_STATUS_RETRIEVED)
    public Mono<ApplicationStatusDTO> retrieveOldApplicationStatus(GetApplicationStatusQuery cmd, ExecutionContext ctx) {
        return queryBus.query(cmd.withApplicationStatusId(ctx.getVariableAs(CTX_LOAN_APPLICATION, LoanApplicationDTO.class).getApplicationStatusId()))
                .doOnNext(oldApplicationStatus -> ctx.putVariable(CTX_OLD_APPLICATION_STATUS, oldApplicationStatus));
    }

    @SagaStep(id = STEP_UPDATE_APPLICATION_STATUS, dependsOn = STEP_RETRIEVE_APPLICATION_STATUS, compensate = "restoreApplicationStatus")
    @StepEvent(type = EVENT_APPLICATION_STATUS_UPDATED)
    public Mono<UUID> updateApplicationStatus(UpdateApplicationStatusCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd
                        .withLoanApplication(ctx.getVariableAs(CTX_LOAN_APPLICATION, LoanApplicationDTO.class))
                        .withApplicationStatusId(ctx.getVariableAs(CTX_APPLICATION_STATUS, ApplicationStatusDTO.class).getApplicationStatusId()));
    }

    public Mono<Void> restoreApplicationStatus(UUID updatedApplicationId, ExecutionContext ctx) {
        ApplicationStatusDTO previous = ctx.getVariableAs(CTX_OLD_APPLICATION_STATUS, ApplicationStatusDTO.class);
        LoanApplicationDTO application = ctx.getVariableAs(CTX_LOAN_APPLICATION, LoanApplicationDTO.class);
        UUID loanApplicationId = application == null ? updatedApplicationId : application.getLoanApplicationId();
        if (previous == null || previous.getApplicationStatusId() == null
                || application == null || loanApplicationId == null) {
            return Mono.empty();
        }
        UpdateApplicationStatusCommand restore = UpdateApplicationStatusCommand.builder()
                .loanApplicationId(loanApplicationId)
                .applicationStatusId(previous.getApplicationStatusId())
                .loanApplication(application)
                .build();
        return commandBus.send(restore).then();
    }

    @SagaStep(id = STEP_UPDATE_APPLICATION_STATUS_HISTORY, dependsOn = STEP_UPDATE_APPLICATION_STATUS, compensate = "removeApplicationStatusHistory")
    @StepEvent(type = EVENT_APPLICATION_STATUS_HISTORY_UPDATED)
    public Mono<UUID> updateApplicationStatusHistory(UpdateApplicationStatusCommand cmd, ExecutionContext ctx) {
        RegisterLoanApplicationStatusHistoryCommand cmdHistory = cmd.getStatusHistoryCommand();
        cmdHistory.setLoanApplicationId(ctx.getVariableAs(CTX_LOAN_APPLICATION, LoanApplicationDTO.class).getLoanApplicationId());
        cmdHistory.setOldStatus(LoanApplicationStatusHistoryDTO.OldStatusEnum.valueOf(ctx.getVariableAs(CTX_OLD_APPLICATION_STATUS, ApplicationStatusDTO.class).getCode()));
        cmdHistory.setNewStatus(LoanApplicationStatusHistoryDTO.NewStatusEnum.valueOf(ctx.getVariableAs(CTX_APPLICATION_STATUS, ApplicationStatusDTO.class).getCode()));
        cmdHistory.setChangeReason("User request to update application status");
        cmdHistory.setChangedBy("system-auto-process");
        cmdHistory.setChangedAt(LocalDateTime.now());
        return commandBus.send(cmdHistory);
    }

    public Mono<Void> removeApplicationStatusHistory(UUID statusHistoryId, ExecutionContext ctx) {
        LoanApplicationDTO application = ctx.getVariableAs(CTX_LOAN_APPLICATION, LoanApplicationDTO.class);
        if (statusHistoryId == null || application == null || application.getLoanApplicationId() == null) {
            return Mono.empty();
        }
        return commandBus.send(new RemoveLoanApplicationStatusHistoryCommand(application.getLoanApplicationId(), statusHistoryId));
    }

}