package io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.handlers;

import io.fireflysoftware.core.lending.origination.sdk.api.ApplicationVerificationApi;
import io.fireflysoftware.core.lending.origination.sdk.model.PaginationResponseApplicationVerificationDTO;
import io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.queries.GetApplicationVerificationsQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@QueryHandlerComponent
public class GetApplicationVerificationsHandler extends QueryHandler<GetApplicationVerificationsQuery, PaginationResponseApplicationVerificationDTO> {

    private final ApplicationVerificationApi applicationVerificationApi;

    @Override
    protected Mono<PaginationResponseApplicationVerificationDTO> doHandle(GetApplicationVerificationsQuery query) {
        return applicationVerificationApi.findAllVerifications(query.getApplicationId(), null, null, null, null, null);
    }
}
