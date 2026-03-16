package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.core.lending.origination.sdk.api.ApplicationTaskApi;
import com.firefly.core.lending.origination.sdk.model.PaginationResponseApplicationTaskDTO;
import com.firefly.domain.lending.loan.origination.core.loan.origination.queries.GetApplicationTasksQuery;
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
class GetApplicationTasksHandlerTest {

    @Mock
    private ApplicationTaskApi applicationTaskApi;

    @InjectMocks
    private GetApplicationTasksHandler handler;

    @Test
    void doHandle_shouldReturnTasks() {
        UUID applicationId = UUID.randomUUID();
        PaginationResponseApplicationTaskDTO expected = new PaginationResponseApplicationTaskDTO();

        when(applicationTaskApi.findAllTasks(applicationId, null, null, null, null, null))
                .thenReturn(Mono.just(expected));

        GetApplicationTasksQuery query = GetApplicationTasksQuery.builder()
                .applicationId(applicationId)
                .build();

        StepVerifier.create(handler.doHandle(query))
                .expectNext(expected)
                .verifyComplete();

        verify(applicationTaskApi).findAllTasks(applicationId, null, null, null, null, null);
    }
}
