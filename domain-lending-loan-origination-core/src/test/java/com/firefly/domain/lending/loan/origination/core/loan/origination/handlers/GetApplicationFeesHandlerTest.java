package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.core.lending.origination.sdk.api.ApplicationFeeApi;
import com.firefly.core.lending.origination.sdk.model.PaginationResponseApplicationFeeDTO;
import com.firefly.domain.lending.loan.origination.core.loan.origination.queries.GetApplicationFeesQuery;
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
class GetApplicationFeesHandlerTest {

    @Mock
    private ApplicationFeeApi applicationFeeApi;

    @InjectMocks
    private GetApplicationFeesHandler handler;

    @Test
    void doHandle_shouldReturnFees() {
        UUID applicationId = UUID.randomUUID();
        PaginationResponseApplicationFeeDTO expected = new PaginationResponseApplicationFeeDTO();

        when(applicationFeeApi.findAllFees(applicationId, null, null, null, null, null))
                .thenReturn(Mono.just(expected));

        GetApplicationFeesQuery query = GetApplicationFeesQuery.builder()
                .applicationId(applicationId)
                .build();

        StepVerifier.create(handler.doHandle(query))
                .expectNext(expected)
                .verifyComplete();

        verify(applicationFeeApi).findAllFees(applicationId, null, null, null, null, null);
    }
}
