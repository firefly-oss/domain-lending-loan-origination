package com.firefly.domain.lending.loan.origination.core.loan.origination.queries;

import com.firefly.common.cqrs.query.Query;
import com.firefly.core.lending.origination.sdk.model.LoanApplicationDTO;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GetLoanApplicationQuery implements Query<LoanApplicationDTO> {
    private UUID loanApplicationId;

    public GetLoanApplicationQuery withLoanApplicationId(UUID loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
        return this;
    }
}