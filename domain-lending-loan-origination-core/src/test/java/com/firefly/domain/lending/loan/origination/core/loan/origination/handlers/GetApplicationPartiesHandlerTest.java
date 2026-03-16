package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.core.lending.origination.sdk.api.ApplicationPartyApi;
import com.firefly.core.lending.origination.sdk.model.PaginationResponseApplicationPartyDTO;
import com.firefly.domain.lending.loan.origination.core.loan.origination.queries.GetApplicationPartiesQuery;
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
class GetApplicationPartiesHandlerTest {

    @Mock
    private ApplicationPartyApi applicationPartyApi;

    @InjectMocks
    private GetApplicationPartiesHandler handler;

    @Test
    void doHandle_shouldReturnParties() {
        UUID applicationId = UUID.randomUUID();
        PaginationResponseApplicationPartyDTO expected = new PaginationResponseApplicationPartyDTO();

        when(applicationPartyApi.findAllParties(applicationId, null, null, null, null, null))
                .thenReturn(Mono.just(expected));

        GetApplicationPartiesQuery query = GetApplicationPartiesQuery.builder()
                .applicationId(applicationId)
                .build();

        StepVerifier.create(handler.doHandle(query))
                .expectNext(expected)
                .verifyComplete();

        verify(applicationPartyApi).findAllParties(applicationId, null, null, null, null, null);
    }
}
