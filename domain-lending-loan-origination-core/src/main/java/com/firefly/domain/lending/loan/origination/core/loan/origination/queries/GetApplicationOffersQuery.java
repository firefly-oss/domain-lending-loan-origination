package com.firefly.domain.lending.loan.origination.core.loan.origination.queries;

import com.firefly.core.lending.origination.sdk.model.PaginationResponseProposedOfferDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fireflyframework.cqrs.query.Query;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetApplicationOffersQuery implements Query<PaginationResponseProposedOfferDTO> {
    private UUID applicationId;
}
