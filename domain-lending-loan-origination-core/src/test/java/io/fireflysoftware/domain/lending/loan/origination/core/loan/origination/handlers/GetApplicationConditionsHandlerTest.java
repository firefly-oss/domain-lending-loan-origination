package io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.handlers;

import io.fireflysoftware.core.lending.origination.sdk.api.ApplicationConditionApi;
import io.fireflysoftware.core.lending.origination.sdk.model.PaginationResponseApplicationConditionDTO;
import io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.queries.GetApplicationConditionsQuery;
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
class GetApplicationConditionsHandlerTest {

    @Mock
    private ApplicationConditionApi applicationConditionApi;

    @InjectMocks
    private GetApplicationConditionsHandler handler;

    @Test
    void doHandle_shouldReturnConditions() {
        UUID applicationId = UUID.randomUUID();
        PaginationResponseApplicationConditionDTO expected = new PaginationResponseApplicationConditionDTO();

        when(applicationConditionApi.findAllConditions(applicationId, null, null, null, null, null))
                .thenReturn(Mono.just(expected));

        GetApplicationConditionsQuery query = GetApplicationConditionsQuery.builder()
                .applicationId(applicationId)
                .build();

        StepVerifier.create(handler.doHandle(query))
                .expectNext(expected)
                .verifyComplete();

        verify(applicationConditionApi).findAllConditions(applicationId, null, null, null, null, null);
    }
}
