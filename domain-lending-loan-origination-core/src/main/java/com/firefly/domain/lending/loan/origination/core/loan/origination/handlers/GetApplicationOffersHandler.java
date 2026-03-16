package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.core.lending.origination.sdk.api.ProposedOfferApi;
import com.firefly.core.lending.origination.sdk.model.PaginationResponseProposedOfferDTO;
import com.firefly.domain.lending.loan.origination.core.loan.origination.queries.GetApplicationOffersQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@QueryHandlerComponent
public class GetApplicationOffersHandler extends QueryHandler<GetApplicationOffersQuery, PaginationResponseProposedOfferDTO> {

    private final ProposedOfferApi proposedOfferApi;

    @Override
    protected Mono<PaginationResponseProposedOfferDTO> doHandle(GetApplicationOffersQuery query) {
        return proposedOfferApi.findAllOffers(query.getApplicationId(), null, null, null, null, null);
    }
}
