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
import io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.commands.RegisterApplicationDocumentCommand;
import io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.commands.RemoveApplicationDocumentCommand;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static io.fireflysoftware.domain.lending.loan.origination.core.loan.utils.constants.GlobalConstants.CTX_LOAN_APPLICATION_ID;
import static io.fireflysoftware.domain.lending.loan.origination.core.loan.utils.constants.RegisterApplicationConstants.*;

@Saga(name = SAGA_REGISTER_APPLICATION_DOCUMENT)
@Service
public class RegisterApplicationDocumentSaga {

    private final CommandBus commandBus;

    @Autowired
    public RegisterApplicationDocumentSaga(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @SagaStep(id = STEP_REGISTER_APPLICATION_DOCUMENT, compensate = COMPENSATE_REMOVE_APPLICATION_DOCUMENT)
    @StepEvent(type = EVENT_APPLICATION_DOCUMENT_REGISTERED)
    public Mono<UUID> registerApplicationDocument(RegisterApplicationDocumentCommand cmd, ExecutionContext ctx) {
        if (cmd.getLoanApplicationId() != null) {
            ctx.putVariable(CTX_LOAN_APPLICATION_ID, cmd.getLoanApplicationId());
        }
        return commandBus.send(cmd);
    }

    public Mono<Void> removeApplicationDocument(UUID applicationDocumentId, ExecutionContext ctx) {
        UUID loanApplicationId = ctx.getVariableAs(CTX_LOAN_APPLICATION_ID, UUID.class);
        if (applicationDocumentId == null || loanApplicationId == null) {
            return Mono.empty();
        }
        return commandBus.send(new RemoveApplicationDocumentCommand(loanApplicationId, applicationDocumentId));
    }
}