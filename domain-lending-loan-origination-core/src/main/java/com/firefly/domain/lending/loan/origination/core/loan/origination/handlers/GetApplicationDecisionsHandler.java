package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.core.lending.origination.sdk.api.UnderwritingDecisionApi;
import com.firefly.core.lending.origination.sdk.model.PaginationResponseUnderwritingDecisionDTO;
import com.firefly.domain.lending.loan.origination.core.loan.origination.queries.GetApplicationDecisionsQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@QueryHandlerComponent
public class GetApplicationDecisionsHandler extends QueryHandler<GetApplicationDecisionsQuery, PaginationResponseUnderwritingDecisionDTO> {

    private final UnderwritingDecisionApi underwritingDecisionApi;

    @Override
    protected Mono<PaginationResponseUnderwritingDecisionDTO> doHandle(GetApplicationDecisionsQuery query) {
        return underwritingDecisionApi.findAllDecisions(query.getApplicationId(), null, null, null, null, null);
    }
}
