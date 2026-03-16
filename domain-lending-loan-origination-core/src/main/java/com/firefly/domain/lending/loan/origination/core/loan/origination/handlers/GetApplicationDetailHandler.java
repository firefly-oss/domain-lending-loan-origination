package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.core.lending.origination.sdk.api.LoanApplicationsApi;
import com.firefly.core.lending.origination.sdk.model.LoanApplicationDTO;
import com.firefly.domain.lending.loan.origination.core.loan.origination.queries.GetApplicationDetailQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@QueryHandlerComponent
public class GetApplicationDetailHandler extends QueryHandler<GetApplicationDetailQuery, LoanApplicationDTO> {

    private final LoanApplicationsApi loanApplicationsApi;

    @Override
    protected Mono<LoanApplicationDTO> doHandle(GetApplicationDetailQuery query) {
        return loanApplicationsApi.getLoanApplication(query.getApplicationId(), null);
    }
}
