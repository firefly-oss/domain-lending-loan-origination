package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.core.lending.origination.sdk.api.ApplicationDocumentApi;
import com.firefly.core.lending.origination.sdk.model.PaginationResponseApplicationDocumentDTO;
import com.firefly.domain.lending.loan.origination.core.loan.origination.queries.GetApplicationDocumentsQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@QueryHandlerComponent
public class GetApplicationDocumentsHandler extends QueryHandler<GetApplicationDocumentsQuery, PaginationResponseApplicationDocumentDTO> {

    private final ApplicationDocumentApi applicationDocumentApi;

    @Override
    protected Mono<PaginationResponseApplicationDocumentDTO> doHandle(GetApplicationDocumentsQuery query) {
        return applicationDocumentApi.findAllDocuments(query.getApplicationId(), null, null, null, null, null);
    }
}
