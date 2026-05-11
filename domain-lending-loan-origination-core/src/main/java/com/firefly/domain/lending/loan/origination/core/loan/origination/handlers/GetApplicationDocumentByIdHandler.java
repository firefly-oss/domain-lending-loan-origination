package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.core.lending.origination.sdk.api.ApplicationDocumentApi;
import com.firefly.core.lending.origination.sdk.model.ApplicationDocumentDTO;
import com.firefly.domain.lending.loan.origination.core.loan.origination.queries.GetApplicationDocumentByIdQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@QueryHandlerComponent
public class GetApplicationDocumentByIdHandler
        extends QueryHandler<GetApplicationDocumentByIdQuery, ApplicationDocumentDTO> {

    private final ApplicationDocumentApi applicationDocumentApi;

    @Override
    protected Mono<ApplicationDocumentDTO> doHandle(GetApplicationDocumentByIdQuery query) {
        return applicationDocumentApi.getDocument(query.getApplicationId(), query.getDocumentId(), null);
    }
}
