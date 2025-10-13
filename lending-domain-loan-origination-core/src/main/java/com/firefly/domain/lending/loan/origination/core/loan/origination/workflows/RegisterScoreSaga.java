package com.firefly.domain.lending.loan.origination.core.loan.origination.workflows;

import com.firefly.common.cqrs.command.CommandBus;
import com.firefly.domain.lending.loan.origination.core.loan.origination.commands.RegisterUnderwritingScoreCommand;
import com.firefly.transactional.annotations.Saga;
import com.firefly.transactional.annotations.SagaStep;
import com.firefly.transactional.annotations.StepEvent;
import com.firefly.transactional.core.SagaContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.firefly.domain.lending.loan.origination.core.loan.utils.constants.GlobalConstants.CTX_LOAN_APPLICATION_ID;
import static com.firefly.domain.lending.loan.origination.core.loan.utils.constants.RegisterApplicationConstants.*;

@Saga(name = SAGA_REGISTER_SCORE)
@Service
public class RegisterScoreSaga {

    private final CommandBus commandBus;

    @Autowired
    public RegisterScoreSaga(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @SagaStep(id = STEP_REGISTER_SCORE)
    @StepEvent(type = EVENT_SCORE_REGISTERED)
    public Mono<UUID> registerScore(RegisterUnderwritingScoreCommand cmd, SagaContext ctx) {
        return commandBus.send(cmd);
    }
}