package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.core.lending.origination.sdk.api.ApplicationDocumentApi;
import com.firefly.core.lending.origination.sdk.model.PaginationResponseApplicationDocumentDTO;
import com.firefly.domain.lending.loan.origination.core.loan.origination.queries.GetApplicationDocumentsQuery;
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
class GetApplicationDocumentsHandlerTest {

    @Mock
    private ApplicationDocumentApi applicationDocumentApi;

    @InjectMocks
    private GetApplicationDocumentsHandler handler;

    @Test
    void doHandle_shouldReturnDocuments() {
        UUID applicationId = UUID.randomUUID();
        PaginationResponseApplicationDocumentDTO expected = new PaginationResponseApplicationDocumentDTO();

        when(applicationDocumentApi.findAllDocuments(applicationId, null, null, null, null, null))
                .thenReturn(Mono.just(expected));

        GetApplicationDocumentsQuery query = GetApplicationDocumentsQuery.builder()
                .applicationId(applicationId)
                .build();

        StepVerifier.create(handler.doHandle(query))
                .expectNext(expected)
                .verifyComplete();

        verify(applicationDocumentApi).findAllDocuments(applicationId, null, null, null, null, null);
    }
}
