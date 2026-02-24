package com.firefly.domain.lending.loan.origination.core.loan.origination.workflows;

import org.fireflyframework.cqrs.command.CommandBus;
import com.firefly.domain.lending.loan.origination.core.loan.origination.commands.*;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.firefly.domain.lending.loan.origination.core.loan.utils.constants.GlobalConstants.CTX_LOAN_APPLICATION_ID;
import static com.firefly.domain.lending.loan.origination.core.loan.utils.constants.RegisterApplicationConstants.*;


@Saga(name = SAGA_REGISTER_APPLICATION)
@Service
public class RegisterApplicationSaga {

    private final CommandBus commandBus;

    @Autowired
    public RegisterApplicationSaga(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @SagaStep(id = STEP_REGISTER_LOAN_APPLICATION, compensate = COMPENSATE_REMOVE_LOAN_APPLICATION)
    @StepEvent(type = EVENT_LOAN_APPLICATION_REGISTERED)
    public Mono<UUID> registerLoanApplication(RegisterLoanApplicationCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd)
                .doOnNext(loanApplicationId -> ctx.putVariable(CTX_LOAN_APPLICATION_ID, loanApplicationId));
    }

    public Mono<Void> removeLoanApplication(UUID loanApplicationId) {
        return commandBus.send(new RemoveLoanApplicationCommand(loanApplicationId));
    }


    @SagaStep(id = STEP_REGISTER_APPLICATION_PARTY, compensate = COMPENSATE_REMOVE_APPLICATION_PARTY, dependsOn = STEP_REGISTER_LOAN_APPLICATION)
    @StepEvent(type = EVENT_APPLICATION_PARTY_REGISTERED)
    public Mono<UUID> registerApplicationParty(RegisterApplicationPartyCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd.withLoanApplicationId(ctx.getVariableAs(CTX_LOAN_APPLICATION_ID, UUID.class)));
    }

    public Mono<Void> removeApplicationParty(UUID applicationPartyId, ExecutionContext ctx) {
        return commandBus.send(new RemoveApplicationPartyCommand(ctx.getVariableAs(CTX_LOAN_APPLICATION_ID, UUID.class), applicationPartyId));
    }

    @SagaStep(id = STEP_REGISTER_APPLICATION_DOCUMENT, compensate = COMPENSATE_REMOVE_APPLICATION_DOCUMENT, dependsOn = STEP_REGISTER_LOAN_APPLICATION)
    @StepEvent(type = EVENT_APPLICATION_DOCUMENT_REGISTERED)
    public Mono<UUID> registerApplicationDocument(RegisterApplicationDocumentCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd.withLoanApplicationId(ctx.getVariableAs(CTX_LOAN_APPLICATION_ID, UUID.class)));
    }

    public Mono<Void> removeApplicationDocument(UUID applicationDocumentId, ExecutionContext ctx) {
        return commandBus.send(new RemoveApplicationDocumentCommand(ctx.getVariableAs(CTX_LOAN_APPLICATION_ID, UUID.class), applicationDocumentId));
    }

    @SagaStep(id = STEP_REGISTER_OFFER, compensate = COMPENSATE_REMOVE_OFFER, dependsOn = STEP_REGISTER_LOAN_APPLICATION)
    @StepEvent(type = EVENT_OFFER_REGISTERED)
    public Mono<UUID> registerOffer(RegisterProposedOfferCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd.withLoanApplicationId(ctx.getVariableAs(CTX_LOAN_APPLICATION_ID, UUID.class)));
    }

    public Mono<Void> removeOffer(UUID proposedOfferId, ExecutionContext ctx) {
        return commandBus.send(new RemoveProposedOfferCommand(ctx.getVariableAs(CTX_LOAN_APPLICATION_ID, UUID.class), proposedOfferId));
    }

    @SagaStep(id = STEP_REGISTER_STATUS, compensate = COMPENSATE_REMOVE_STATUS, dependsOn = STEP_REGISTER_LOAN_APPLICATION)
    @StepEvent(type = EVENT_STATUS_REGISTERED)
    public Mono<UUID> registerStatus(RegisterLoanApplicationStatusHistoryCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd.withLoanApplicationId(ctx.getVariableAs(CTX_LOAN_APPLICATION_ID, UUID.class)));
    }

    public Mono<Void> removeStatus(UUID statusHistoryId, ExecutionContext ctx) {
        return commandBus.send(new RemoveLoanApplicationStatusHistoryCommand(ctx.getVariableAs(CTX_LOAN_APPLICATION_ID, UUID.class), statusHistoryId));
    }

    @SagaStep(id = STEP_REGISTER_SCORE, compensate = COMPENSATE_REMOVE_SCORE, dependsOn = STEP_REGISTER_LOAN_APPLICATION)
    @StepEvent(type = EVENT_SCORE_REGISTERED)
    public Mono<UUID> registerScore(RegisterUnderwritingScoreCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd.withLoanApplicationId(ctx.getVariableAs(CTX_LOAN_APPLICATION_ID, UUID.class)));
    }

    public Mono<Void> removeScore(UUID underwritingScoreId, ExecutionContext ctx) {
        return commandBus.send(new RemoveUnderwritingScoreCommand(ctx.getVariableAs(CTX_LOAN_APPLICATION_ID, UUID.class), underwritingScoreId));
    }

    @SagaStep(id = STEP_REGISTER_DECISION, compensate = COMPENSATE_REMOVE_DECISION, dependsOn = STEP_REGISTER_LOAN_APPLICATION)
    @StepEvent(type = EVENT_DECISION_REGISTERED)
    public Mono<UUID> registerDecision(RegisterUnderwritingDecisionCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd.withLoanApplicationId(ctx.getVariableAs(CTX_LOAN_APPLICATION_ID, UUID.class)));
    }

    public Mono<Void> removeDecision(UUID underwritingDecisionId, ExecutionContext ctx) {
        return commandBus.send(new RemoveUnderwritingDecisionCommand(ctx.getVariableAs(CTX_LOAN_APPLICATION_ID, UUID.class), underwritingDecisionId));
    }

}
