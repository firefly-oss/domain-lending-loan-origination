package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.core.lending.origination.sdk.api.ApplicationFeeApi;
import com.firefly.core.lending.origination.sdk.model.PaginationResponseApplicationFeeDTO;
import com.firefly.domain.lending.loan.origination.core.loan.origination.queries.GetApplicationFeesQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@QueryHandlerComponent
public class GetApplicationFeesHandler extends QueryHandler<GetApplicationFeesQuery, PaginationResponseApplicationFeeDTO> {

    private final ApplicationFeeApi applicationFeeApi;

    @Override
    protected Mono<PaginationResponseApplicationFeeDTO> doHandle(GetApplicationFeesQuery query) {
        return applicationFeeApi.findAllFees(query.getApplicationId(), null, null, null, null, null);
    }
}
