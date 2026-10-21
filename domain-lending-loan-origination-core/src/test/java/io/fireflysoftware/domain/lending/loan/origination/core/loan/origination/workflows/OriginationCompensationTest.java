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

import io.fireflysoftware.core.lending.origination.sdk.model.ApplicationStatusDTO;
import io.fireflysoftware.core.lending.origination.sdk.model.LoanApplicationDTO;
import io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.commands.RegisterApplicationDocumentCommand;
import io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.commands.RegisterUnderwritingScoreCommand;
import io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.commands.RemoveApplicationDocumentCommand;
import io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.commands.RemoveLoanApplicationStatusHistoryCommand;
import io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.commands.RemoveUnderwritingScoreCommand;
import io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.commands.UpdateApplicationStatusCommand;
import io.fireflysoftware.domain.lending.loan.origination.core.loan.utils.constants.GlobalConstants;
import org.fireflyframework.cqrs.command.Command;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Standalone origination writes use the same remove and restore commands as registration.
 */
@ExtendWith(MockitoExtension.class)
class OriginationCompensationTest {

    @Mock
    private CommandBus commandBus;

    @Test
    void scoreRegistrationDeletesTheCreatedScore() {
        UUID loanApplicationId = UUID.randomUUID();
        UUID scoreId = UUID.randomUUID();
        when(commandBus.send(any(RegisterUnderwritingScoreCommand.class))).thenReturn(Mono.just(scoreId));
        when(commandBus.send(any(RemoveUnderwritingScoreCommand.class))).thenReturn(Mono.empty());
        RegisterScoreSaga saga = new RegisterScoreSaga(commandBus);
        ExecutionContext ctx = ExecutionContext.forSaga("RegisterScoreSaga", "test");
        RegisterUnderwritingScoreCommand command = new RegisterUnderwritingScoreCommand();
        command.setLoanApplicationId(loanApplicationId);

        StepVerifier.create(saga.registerScore(command, ctx)).expectNext(scoreId).verifyComplete();
        StepVerifier.create(saga.removeScore(scoreId, ctx)).verifyComplete();

        ArgumentCaptor<RemoveUnderwritingScoreCommand> captor = ArgumentCaptor.forClass(RemoveUnderwritingScoreCommand.class);
        verify(commandBus).send(captor.capture());
        assertThat(captor.getValue().loanApplicationId()).isEqualTo(loanApplicationId);
        assertThat(captor.getValue().underwritingScoreId()).isEqualTo(scoreId);
    }

    @Test
    void documentRegistrationDeletesTheCreatedDocument() {
        UUID loanApplicationId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();
        when(commandBus.send(any(RegisterApplicationDocumentCommand.class))).thenReturn(Mono.just(documentId));
        when(commandBus.send(any(RemoveApplicationDocumentCommand.class))).thenReturn(Mono.empty());
        RegisterApplicationDocumentSaga saga = new RegisterApplicationDocumentSaga(commandBus);
        ExecutionContext ctx = ExecutionContext.forSaga("RegisterApplicationDocumentSaga", "test");
        RegisterApplicationDocumentCommand command = new RegisterApplicationDocumentCommand();
        command.setLoanApplicationId(loanApplicationId);

        StepVerifier.create(saga.registerApplicationDocument(command, ctx)).expectNext(documentId).verifyComplete();
        StepVerifier.create(saga.removeApplicationDocument(documentId, ctx)).verifyComplete();

        ArgumentCaptor<RemoveApplicationDocumentCommand> captor = ArgumentCaptor.forClass(RemoveApplicationDocumentCommand.class);
        verify(commandBus).send(captor.capture());
        assertThat(captor.getValue().loanApplicationId()).isEqualTo(loanApplicationId);
        assertThat(captor.getValue().applicationDocumentId()).isEqualTo(documentId);
    }

    @Test
    void statusChangeRestoresThePreviousStatusAndDropsTheHistoryRow() {
        UUID loanApplicationId = UUID.randomUUID();
        UUID previousStatusId = UUID.randomUUID();
        UUID historyId = UUID.randomUUID();
        LoanApplicationDTO application = new LoanApplicationDTO(loanApplicationId);
        LocalDateTime now = LocalDateTime.now();
        ApplicationStatusDTO previous = new ApplicationStatusDTO(previousStatusId, now, now);
        when(commandBus.send(any(UpdateApplicationStatusCommand.class))).thenReturn(Mono.just(loanApplicationId));
        when(commandBus.send(any(RemoveLoanApplicationStatusHistoryCommand.class))).thenReturn(Mono.empty());

        UpdateApplicationStatusSaga saga = new UpdateApplicationStatusSaga(commandBus, null);
        ExecutionContext ctx = ExecutionContext.forSaga("UpdateApplicationStatusSaga", "test");
        ctx.putVariable(GlobalConstants.CTX_LOAN_APPLICATION, application);
        ctx.putVariable(GlobalConstants.CTX_OLD_APPLICATION_STATUS, previous);

        StepVerifier.create(saga.restoreApplicationStatus(loanApplicationId, ctx)).verifyComplete();
        StepVerifier.create(saga.removeApplicationStatusHistory(historyId, ctx)).verifyComplete();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Command<?>> sent = ArgumentCaptor.forClass(Command.class);
        verify(commandBus, times(2)).send(sent.capture());
        UpdateApplicationStatusCommand status = (UpdateApplicationStatusCommand) sent.getAllValues().get(0);
        RemoveLoanApplicationStatusHistoryCommand history =
                (RemoveLoanApplicationStatusHistoryCommand) sent.getAllValues().get(1);
        assertThat(status.getLoanApplicationId()).isEqualTo(loanApplicationId);
        assertThat(status.getApplicationStatusId()).isEqualTo(previousStatusId);
        assertThat(history.loanApplicationId()).isEqualTo(loanApplicationId);
        assertThat(history.statusHistoryId()).isEqualTo(historyId);
    }
}
