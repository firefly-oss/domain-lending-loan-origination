package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.common.cqrs.annotations.QueryHandlerComponent;
import com.firefly.common.cqrs.query.QueryHandler;
import com.firefly.core.lending.origination.sdk.api.LoanApplicationsApi;
import com.firefly.core.lending.origination.sdk.model.LoanApplicationDTO;
import com.firefly.domain.lending.loan.origination.core.loan.origination.queries.GetLoanApplicationQuery;
import reactor.core.publisher.Mono;

@QueryHandlerComponent
public class GetLoanApplicationHandler extends QueryHandler<GetLoanApplicationQuery, LoanApplicationDTO> {

    private final LoanApplicationsApi loanApplicationsApi;

    public GetLoanApplicationHandler(LoanApplicationsApi loanApplicationsApi) {
        this.loanApplicationsApi = loanApplicationsApi;
    }

    @Override
    protected Mono<LoanApplicationDTO> doHandle(GetLoanApplicationQuery cmd) {
        return loanApplicationsApi.getLoanApplication(cmd.getLoanApplicationId());
    }
}