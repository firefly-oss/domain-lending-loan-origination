package com.firefly.domain.lending.loan.origination.core.loan.origination.services.impl;

import com.firefly.common.cqrs.query.QueryBus;
import com.firefly.core.lending.origination.sdk.model.LoanApplicationDTO;
import com.firefly.domain.lending.loan.origination.core.loan.origination.commands.*;
import com.firefly.domain.lending.loan.origination.core.loan.origination.queries.GetLoanApplicationQuery;
import com.firefly.domain.lending.loan.origination.core.loan.origination.services.LoanOriginationService;
import com.firefly.domain.lending.loan.origination.core.loan.origination.workflows.RegisterApplicationSaga;
import com.firefly.domain.lending.loan.origination.core.loan.origination.workflows.RegisterApplicationDocumentSaga;
import com.firefly.domain.lending.loan.origination.core.loan.origination.workflows.RegisterScoreSaga;
import com.firefly.domain.lending.loan.origination.core.loan.origination.workflows.UpdateApplicationStatusSaga;
import com.firefly.transactional.core.SagaResult;
import com.firefly.transactional.engine.ExpandEach;
import com.firefly.transactional.engine.SagaEngine;
import com.firefly.transactional.engine.StepInputs;
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
                .forStep(RegisterApplicationSaga::registerLoanApplication, command.getApplication())
                .forStep(RegisterApplicationSaga::registerApplicationParty, ExpandEach.of(command.getParties()))
                .forStep(RegisterApplicationSaga::registerApplicationDocument, ExpandEach.of(command.getDocuments()))
                .forStep(RegisterApplicationSaga::registerCollateral, ExpandEach.of(command.getCollaterals()))
                .forStep(RegisterApplicationSaga::registerOffer, ExpandEach.of(command.getOffers()))
                .forStep(RegisterApplicationSaga::registerStatus, ExpandEach.of(command.getStatusHistories()))
                .forStep(RegisterApplicationSaga::registerScore, command.getScore())
                .forStep(RegisterApplicationSaga::registerDecision, command.getDecision())
                .build();

        return engine.execute(RegisterApplicationSaga.class, inputs);
    }

    @Override
    public Mono<SagaResult> attachDocuments(UUID appId, RegisterApplicationDocumentCommand command) {
        StepInputs inputs = StepInputs.builder()
                .forStep(RegisterApplicationDocumentSaga::registerApplicationDocument, command.withLoanApplicationId(appId))
                .build();

        return engine.execute(RegisterApplicationDocumentSaga.class, inputs);
    }

    @Override
    public Mono<SagaResult> scoreApplication(UUID appId, RegisterUnderwritingScoreCommand command) {
        StepInputs inputs = StepInputs.builder()
                .forStep(RegisterScoreSaga::registerScore, command.withLoanApplicationId(appId))
                .build();

        return engine.execute(RegisterScoreSaga.class, inputs);
    }

    @Override
    public Mono<SagaResult> updateApplicationStatus(UpdateApplicationStatusCommand command) {
        StepInputs inputs = StepInputs.builder()
                .forStep(UpdateApplicationStatusSaga::retrieveApplicationStatus, command.getApplicationStatusQuery())
                .forStep(UpdateApplicationStatusSaga::retrieveLoanApplication, command.getApplicationQuery())
                .forStep(UpdateApplicationStatusSaga::retrieveOldApplicationStatus, command.getApplicationStatusQuery())
                .forStep(UpdateApplicationStatusSaga::updateApplicationStatus, command)
                .forStep(UpdateApplicationStatusSaga::updateApplicationStatusHistory, command)
                .build();

        return engine.execute(UpdateApplicationStatusSaga.class, inputs);
    }

    @Override
    public Mono<LoanApplicationDTO> getApplication(UUID appId) {
        return queryBus.query(GetLoanApplicationQuery.builder().loanApplicationId(appId).build());
    }
}
