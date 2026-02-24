package com.firefly.domain.lending.loan.origination.core.loan.origination.services.impl;

import org.fireflyframework.cqrs.query.QueryBus;
import com.firefly.core.lending.origination.sdk.model.LoanApplicationDTO;
import com.firefly.domain.lending.loan.origination.core.loan.origination.commands.*;
import com.firefly.domain.lending.loan.origination.core.loan.origination.queries.GetLoanApplicationQuery;
import com.firefly.domain.lending.loan.origination.core.loan.origination.services.LoanOriginationService;
import com.firefly.domain.lending.loan.origination.core.loan.origination.workflows.RegisterApplicationSaga;
import com.firefly.domain.lending.loan.origination.core.loan.origination.workflows.RegisterApplicationDocumentSaga;
import com.firefly.domain.lending.loan.origination.core.loan.origination.workflows.RegisterScoreSaga;
import com.firefly.domain.lending.loan.origination.core.loan.origination.workflows.UpdateApplicationStatusSaga;
import org.fireflyframework.orchestration.saga.engine.SagaResult;
import org.fireflyframework.orchestration.saga.engine.ExpandEach;
import org.fireflyframework.orchestration.saga.engine.SagaEngine;
import org.fireflyframework.orchestration.saga.engine.StepInputs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class LoanOriginationServiceImpl implements LoanOriginationService {

    private final SagaEngine engine;
    private final QueryBus queryBus;

    @Autowired
    public LoanOriginationServiceImpl(SagaEngine engine, QueryBus queryBus){
        this.engine=engine;
        this.queryBus = queryBus;
    }

    @Override
    public Mono<SagaResult> submitApplication(SubmitApplicationCommand command) {
        StepInputs inputs = StepInputs.builder()
                .forStepId("registerLoanApplication", command.getApplication())
                .forStepId("registerApplicationParty", ExpandEach.of(command.getParties()))
                .forStepId("registerApplicationDocument", ExpandEach.of(command.getDocuments()))
                .forStepId("registerOffer", ExpandEach.of(command.getOffers()))
                .forStepId("registerStatus", ExpandEach.of(command.getStatusHistories()))
                .forStepId("registerScore", command.getScore())
                .forStepId("registerDecision", command.getDecision())
                .build();

        return engine.execute("RegisterApplicationSaga", inputs);
    }

    @Override
    public Mono<SagaResult> attachDocuments(UUID appId, RegisterApplicationDocumentCommand command) {
        StepInputs inputs = StepInputs.builder()
                .forStepId("registerApplicationDocument", command.withLoanApplicationId(appId))
                .build();

        return engine.execute("RegisterApplicationDocumentSaga", inputs);
    }

    @Override
    public Mono<SagaResult> scoreApplication(UUID appId, RegisterUnderwritingScoreCommand command) {
        StepInputs inputs = StepInputs.builder()
                .forStepId("registerScore", command.withLoanApplicationId(appId))
                .build();

        return engine.execute("RegisterScoreSaga", inputs);
    }

    @Override
    public Mono<SagaResult> updateApplicationStatus(UpdateApplicationStatusCommand command) {
        StepInputs inputs = StepInputs.builder()
                .forStepId("retrieveApplicationStatus", command.getApplicationStatusQuery())
                .forStepId("retrieveApplication", command.getApplicationQuery())
                .forStepId("retrieveOldApplicationStatus", command.getApplicationStatusQuery())
                .forStepId("updateApplicationStatus", command)
                .forStepId("updateApplicationStatusHistory", command)
                .build();

        return engine.execute("UpdateApplicationStatusSaga", inputs);
    }

    @Override
    public Mono<LoanApplicationDTO> getApplication(UUID appId) {
        return queryBus.query(GetLoanApplicationQuery.builder().loanApplicationId(appId).build());
    }
}
