package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.core.lending.origination.sdk.api.ApplicationPartyApi;
import com.firefly.core.lending.origination.sdk.model.PaginationResponseApplicationPartyDTO;
import com.firefly.domain.lending.loan.origination.core.loan.origination.queries.GetApplicationPartiesQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@QueryHandlerComponent
public class GetApplicationPartiesHandler extends QueryHandler<GetApplicationPartiesQuery, PaginationResponseApplicationPartyDTO> {

    private final ApplicationPartyApi applicationPartyApi;

    @Override
    protected Mono<PaginationResponseApplicationPartyDTO> doHandle(GetApplicationPartiesQuery query) {
        return applicationPartyApi.findAllParties(query.getApplicationId(), null, null, null, null, null);
    }
}
