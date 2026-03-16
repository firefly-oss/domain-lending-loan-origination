package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.core.lending.origination.sdk.api.ApplicationTaskApi;
import com.firefly.core.lending.origination.sdk.model.PaginationResponseApplicationTaskDTO;
import com.firefly.domain.lending.loan.origination.core.loan.origination.queries.GetApplicationTasksQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@QueryHandlerComponent
public class GetApplicationTasksHandler extends QueryHandler<GetApplicationTasksQuery, PaginationResponseApplicationTaskDTO> {

    private final ApplicationTaskApi applicationTaskApi;

    @Override
    protected Mono<PaginationResponseApplicationTaskDTO> doHandle(GetApplicationTasksQuery query) {
        return applicationTaskApi.findAllTasks(query.getApplicationId(), null, null, null, null, null);
    }
}
