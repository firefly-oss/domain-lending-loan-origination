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

package com.firefly.domain.lending.loan.origination.core.loan.origination.workflows;

import com.firefly.domain.lending.loan.origination.core.loan.origination.commands.RegisterLoanApplicationCommand;
import com.firefly.domain.lending.loan.origination.core.loan.utils.constants.ApplicationLookupDefaults;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link RegisterApplicationSaga}'s default-FK injection behaviour
 * (Defect D / Wave 2).
 *
 * <p>The core service's {@code LoanApplicationDTO} marks
 * {@code applicationStatusId}, {@code applicationSubStatusId}, and
 * {@code submissionChannelId} as {@code @NotNull}. The experience BFF does not
 * supply them today, so the saga must inject canonical defaults before the
 * command leaves the domain tier.
 */
@ExtendWith(MockitoExtension.class)
class RegisterApplicationSagaTest {

    @Mock
    private CommandBus commandBus;

    private RegisterApplicationSaga saga;

    @BeforeEach
    void setUp() {
        saga = new RegisterApplicationSaga(commandBus);
    }

    @Test
    void registerLoanApplication_populatesAllThreeLookupDefaults_whenIncomingFieldsAreNull() {
        var cmd = new RegisterLoanApplicationCommand();
        // applicationStatusId, applicationSubStatusId, submissionChannelId left null,
        // mirroring the experience BFF's minimal RegisterLoanApplicationCommand.

        UUID generatedId = UUID.randomUUID();
        when(commandBus.send(any(RegisterLoanApplicationCommand.class)))
                .thenReturn(Mono.just(generatedId));

        ExecutionContext ctx = mock(ExecutionContext.class);

        StepVerifier.create(saga.registerLoanApplication(cmd, ctx))
                .expectNext(generatedId)
                .verifyComplete();

        ArgumentCaptor<RegisterLoanApplicationCommand> captor =
                ArgumentCaptor.forClass(RegisterLoanApplicationCommand.class);
        verify(commandBus).send(captor.capture());
        RegisterLoanApplicationCommand sent = captor.getValue();

        assertThat(sent.getApplicationStatusId())
                .isEqualTo(ApplicationLookupDefaults.DEFAULT_APPLICATION_STATUS_ID);
        assertThat(sent.getApplicationSubStatusId())
                .isEqualTo(ApplicationLookupDefaults.DEFAULT_APPLICATION_SUB_STATUS_ID);
        assertThat(sent.getSubmissionChannelId())
                .isEqualTo(ApplicationLookupDefaults.DEFAULT_SUBMISSION_CHANNEL_ID);
    }

    @Test
    void registerLoanApplication_preservesCallerSuppliedIds_whenAllThreeAreProvided() {
        UUID callerStatus = UUID.randomUUID();
        UUID callerSubStatus = UUID.randomUUID();
        UUID callerChannel = UUID.randomUUID();

        var cmd = new RegisterLoanApplicationCommand();
        cmd.setApplicationStatusId(callerStatus);
        cmd.setApplicationSubStatusId(callerSubStatus);
        cmd.setSubmissionChannelId(callerChannel);

        UUID generatedId = UUID.randomUUID();
        when(commandBus.send(any(RegisterLoanApplicationCommand.class)))
                .thenReturn(Mono.just(generatedId));

        ExecutionContext ctx = mock(ExecutionContext.class);

        StepVerifier.create(saga.registerLoanApplication(cmd, ctx))
                .expectNext(generatedId)
                .verifyComplete();

        ArgumentCaptor<RegisterLoanApplicationCommand> captor =
                ArgumentCaptor.forClass(RegisterLoanApplicationCommand.class);
        verify(commandBus).send(captor.capture());
        RegisterLoanApplicationCommand sent = captor.getValue();

        assertThat(sent.getApplicationStatusId()).isEqualTo(callerStatus);
        assertThat(sent.getApplicationSubStatusId()).isEqualTo(callerSubStatus);
        assertThat(sent.getSubmissionChannelId()).isEqualTo(callerChannel);
    }

    @Test
    void registerLoanApplication_fillsOnlyTheNullSlots_whenSomeIdsAreSupplied() {
        UUID callerStatus = UUID.randomUUID();

        var cmd = new RegisterLoanApplicationCommand();
        cmd.setApplicationStatusId(callerStatus);
        // applicationSubStatusId and submissionChannelId left null

        UUID generatedId = UUID.randomUUID();
        when(commandBus.send(any(RegisterLoanApplicationCommand.class)))
                .thenReturn(Mono.just(generatedId));

        ExecutionContext ctx = mock(ExecutionContext.class);

        StepVerifier.create(saga.registerLoanApplication(cmd, ctx))
                .expectNext(generatedId)
                .verifyComplete();

        ArgumentCaptor<RegisterLoanApplicationCommand> captor =
                ArgumentCaptor.forClass(RegisterLoanApplicationCommand.class);
        verify(commandBus).send(captor.capture());
        RegisterLoanApplicationCommand sent = captor.getValue();

        assertThat(sent.getApplicationStatusId()).isEqualTo(callerStatus);
        assertThat(sent.getApplicationSubStatusId())
                .isEqualTo(ApplicationLookupDefaults.DEFAULT_APPLICATION_SUB_STATUS_ID);
        assertThat(sent.getSubmissionChannelId())
                .isEqualTo(ApplicationLookupDefaults.DEFAULT_SUBMISSION_CHANNEL_ID);
    }

    @Test
    void registerLoanApplication_storesGeneratedIdInExecutionContext() {
        var cmd = new RegisterLoanApplicationCommand();
        UUID generatedId = UUID.randomUUID();
        when(commandBus.send(any(RegisterLoanApplicationCommand.class)))
                .thenReturn(Mono.just(generatedId));

        ExecutionContext ctx = mock(ExecutionContext.class);

        StepVerifier.create(saga.registerLoanApplication(cmd, ctx))
                .expectNext(generatedId)
                .verifyComplete();

        // The saga must publish the generated id under the well-known key so
        // dependent steps (registerApplicationParty, registerApplicationDocument,
        // registerOffer, registerStatus, registerScore, registerDecision) can
        // pick it up via ctx.getVariableAs(...).
        verify(ctx).putVariable("loanApplicationId", generatedId);
    }
}
