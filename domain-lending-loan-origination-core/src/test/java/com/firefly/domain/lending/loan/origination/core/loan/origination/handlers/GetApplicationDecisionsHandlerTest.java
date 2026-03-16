package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.core.lending.origination.sdk.api.UnderwritingDecisionApi;
import com.firefly.core.lending.origination.sdk.model.PaginationResponseUnderwritingDecisionDTO;
import com.firefly.domain.lending.loan.origination.core.loan.origination.queries.GetApplicationDecisionsQuery;
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
class GetApplicationDecisionsHandlerTest {

    @Mock
    private UnderwritingDecisionApi underwritingDecisionApi;

    @InjectMocks
    private GetApplicationDecisionsHandler handler;

    @Test
    void doHandle_shouldReturnDecisions() {
        UUID applicationId = UUID.randomUUID();
        PaginationResponseUnderwritingDecisionDTO expected = new PaginationResponseUnderwritingDecisionDTO();

        when(underwritingDecisionApi.findAllDecisions(applicationId, null, null, null, null, null))
                .thenReturn(Mono.just(expected));

        GetApplicationDecisionsQuery query = GetApplicationDecisionsQuery.builder()
                .applicationId(applicationId)
                .build();

        StepVerifier.create(handler.doHandle(query))
                .expectNext(expected)
                .verifyComplete();

        verify(underwritingDecisionApi).findAllDecisions(applicationId, null, null, null, null, null);
    }
}
