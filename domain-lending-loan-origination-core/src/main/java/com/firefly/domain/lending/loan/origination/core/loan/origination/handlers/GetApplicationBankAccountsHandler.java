package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.core.lending.origination.sdk.api.ApplicationExternalBankAccountsApi;
import com.firefly.core.lending.origination.sdk.model.PaginationResponseApplicationExternalBankAccountDTO;
import com.firefly.domain.lending.loan.origination.core.loan.origination.queries.GetApplicationBankAccountsQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@QueryHandlerComponent
public class GetApplicationBankAccountsHandler extends QueryHandler<GetApplicationBankAccountsQuery, PaginationResponseApplicationExternalBankAccountDTO> {

    private final ApplicationExternalBankAccountsApi applicationExternalBankAccountsApi;

    @Override
    protected Mono<PaginationResponseApplicationExternalBankAccountDTO> doHandle(GetApplicationBankAccountsQuery query) {
        return applicationExternalBankAccountsApi.findAll(query.getApplicationId(), null, null);
    }
}
