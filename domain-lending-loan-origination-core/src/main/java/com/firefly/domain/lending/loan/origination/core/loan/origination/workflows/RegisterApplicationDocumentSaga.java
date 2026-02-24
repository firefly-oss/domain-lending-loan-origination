package com.firefly.domain.lending.loan.origination.core.loan.origination.workflows;

import org.fireflyframework.cqrs.command.CommandBus;
import com.firefly.domain.lending.loan.origination.core.loan.origination.commands.RegisterApplicationDocumentCommand;
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

@Saga(name = SAGA_REGISTER_APPLICATION_DOCUMENT)
@Service
public class RegisterApplicationDocumentSaga {

    private final CommandBus commandBus;

    @Autowired
    public RegisterApplicationDocumentSaga(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @SagaStep(id = STEP_REGISTER_APPLICATION_DOCUMENT)
    @StepEvent(type = EVENT_APPLICATION_DOCUMENT_REGISTERED)
    public Mono<UUID> registerApplicationDocument(RegisterApplicationDocumentCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd);
    }
}