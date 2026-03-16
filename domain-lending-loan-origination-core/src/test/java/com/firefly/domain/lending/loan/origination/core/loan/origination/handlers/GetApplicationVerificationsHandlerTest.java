package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.core.lending.origination.sdk.api.ApplicationVerificationApi;
import com.firefly.core.lending.origination.sdk.model.PaginationResponseApplicationVerificationDTO;
import com.firefly.domain.lending.loan.origination.core.loan.origination.queries.GetApplicationVerificationsQuery;
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
class GetApplicationVerificationsHandlerTest {

    @Mock
    private ApplicationVerificationApi applicationVerificationApi;

    @InjectMocks
    private GetApplicationVerificationsHandler handler;

    @Test
    void doHandle_shouldReturnVerifications() {
        UUID applicationId = UUID.randomUUID();
        PaginationResponseApplicationVerificationDTO expected = new PaginationResponseApplicationVerificationDTO();

        when(applicationVerificationApi.findAllVerifications(applicationId, null, null, null, null, null))
                .thenReturn(Mono.just(expected));

        GetApplicationVerificationsQuery query = GetApplicationVerificationsQuery.builder()
                .applicationId(applicationId)
                .build();

        StepVerifier.create(handler.doHandle(query))
                .expectNext(expected)
                .verifyComplete();

        verify(applicationVerificationApi).findAllVerifications(applicationId, null, null, null, null, null);
    }
}
