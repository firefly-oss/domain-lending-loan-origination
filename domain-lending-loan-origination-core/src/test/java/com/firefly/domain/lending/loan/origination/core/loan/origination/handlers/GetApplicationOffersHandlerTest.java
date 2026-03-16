package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.core.lending.origination.sdk.api.ProposedOfferApi;
import com.firefly.core.lending.origination.sdk.model.PaginationResponseProposedOfferDTO;
import com.firefly.domain.lending.loan.origination.core.loan.origination.queries.GetApplicationOffersQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetApplicationOffersHandlerTest {

    @Mock
    private ProposedOfferApi proposedOfferApi;

    @InjectMocks
    private GetApplicationOffersHandler handler;

    @Test
    void doHandle_shouldReturnOffers() {
        UUID applicationId = UUID.randomUUID();
        PaginationResponseProposedOfferDTO expected = new PaginationResponseProposedOfferDTO();

        when(proposedOfferApi.findAllOffers(applicationId, null, null, null, null, null))
                .thenReturn(Mono.just(expected));

        GetApplicationOffersQuery query = GetApplicationOffersQuery.builder()
                .applicationId(applicationId)
                .build();

        StepVerifier.create(handler.doHandle(query))
                .expectNext(expected)
                .verifyComplete();

        verify(proposedOfferApi).findAllOffers(applicationId, null, null, null, null, null);
    }
}
