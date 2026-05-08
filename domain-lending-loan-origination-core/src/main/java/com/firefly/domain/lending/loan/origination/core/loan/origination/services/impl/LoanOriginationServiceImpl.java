package com.firefly.domain.lending.loan.origination.core.loan.origination.services.impl;

import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.cqrs.query.QueryBus;
import com.firefly.core.lending.origination.sdk.model.ApplicationPartyDTO;
import com.firefly.core.lending.origination.sdk.model.LoanApplicationDTO;
import com.firefly.core.lending.origination.sdk.model.SimulationDTO;
import com.firefly.domain.lending.loan.origination.core.applicationparty.commands.UpdateApplicationEmploymentDataCommand;
import com.firefly.domain.lending.loan.origination.core.loan.origination.commands.*;
import com.firefly.domain.lending.loan.origination.core.loan.origination.queries.GetLoanApplicationQuery;
import com.firefly.domain.lending.loan.origination.core.loan.origination.services.LoanOriginationService;
import com.firefly.domain.lending.loan.origination.core.loan.origination.workflows.RegisterApplicationSaga;
import com.firefly.domain.lending.loan.origination.core.loan.origination.workflows.RegisterApplicationDocumentSaga;
import com.firefly.domain.lending.loan.origination.core.loan.origination.workflows.RegisterScoreSaga;
import com.firefly.domain.lending.loan.origination.core.loan.origination.workflows.UpdateApplicationStatusSaga;
import com.firefly.domain.lending.loan.origination.core.simulation.commands.PersistSimulationCommand;
import org.fireflyframework.orchestration.saga.engine.SagaResult;
import org.fireflyframework.orchestration.saga.engine.ExpandEach;
import org.fireflyframework.orchestration.saga.engine.SagaEngine;
import org.fireflyframework.orchestration.saga.engine.StepInputs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
public class LoanOriginationServiceImpl implements LoanOriginationService {

    private final SagaEngine engine;
    private final QueryBus queryBus;
    private final CommandBus commandBus;

    @Autowired
    public LoanOriginationServiceImpl(SagaEngine engine, QueryBus queryBus, CommandBus commandBus){
        this.engine=engine;
        this.queryBus = queryBus;
        this.commandBus = commandBus;
    }

    @Override
    public Mono<SagaResult> submitApplication(SubmitApplicationCommand command) {
        // Defensively default the optional collections so callers (notably the experience tier)
        // can post a minimal SubmitApplicationCommand with only application set, without
        // tripping ExpandEach.of(...) on null. Each collection is independent and optional
        // for the saga's per-step expansion.
        StepInputs inputs = StepInputs.builder()
                .forStepId("registerLoanApplication", command.getApplication())
                .forStepId("registerApplicationParty", ExpandEach.of(nullSafe(command.getParties())))
                .forStepId("registerApplicationDocument", ExpandEach.of(nullSafe(command.getDocuments())))
                .forStepId("registerOffer", ExpandEach.of(nullSafe(command.getOffers())))
                .forStepId("registerStatus", ExpandEach.of(nullSafe(command.getStatusHistories())))
                .forStepId("registerScore", command.getScore())
                .forStepId("registerDecision", command.getDecision())
                .build();

        return engine.execute("RegisterApplicationSaga", inputs);
    }

    private static <T> List<T> nullSafe(List<T> list) {
        return list == null ? List.of() : list;
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

    @Override
    public Mono<SimulationDTO> persistSimulation(PersistSimulationCommand cmd) {
        return commandBus.send(cmd);
    }

    @Override
    public Mono<ApplicationPartyDTO> updateApplicationEmploymentData(UpdateApplicationEmploymentDataCommand cmd) {
        return commandBus.send(cmd);
    }
}
