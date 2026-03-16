package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.core.lending.origination.sdk.api.ApplicationConditionApi;
import com.firefly.core.lending.origination.sdk.model.PaginationResponseApplicationConditionDTO;
import com.firefly.domain.lending.loan.origination.core.loan.origination.queries.GetApplicationConditionsQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@QueryHandlerComponent
public class GetApplicationConditionsHandler extends QueryHandler<GetApplicationConditionsQuery, PaginationResponseApplicationConditionDTO> {

    private final ApplicationConditionApi applicationConditionApi;

    @Override
    protected Mono<PaginationResponseApplicationConditionDTO> doHandle(GetApplicationConditionsQuery query) {
        return applicationConditionApi.findAllConditions(query.getApplicationId(), null, null, null, null, null);
    }
}
