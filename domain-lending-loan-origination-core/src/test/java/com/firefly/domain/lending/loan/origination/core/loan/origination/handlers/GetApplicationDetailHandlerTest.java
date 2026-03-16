package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.core.lending.origination.sdk.api.LoanApplicationsApi;
import com.firefly.core.lending.origination.sdk.model.LoanApplicationDTO;
import com.firefly.domain.lending.loan.origination.core.loan.origination.queries.GetApplicationDetailQuery;
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
class GetApplicationDetailHandlerTest {

    @Mock
    private LoanApplicationsApi loanApplicationsApi;

    @InjectMocks
    private GetApplicationDetailHandler handler;

    @Test
    void doHandle_shouldReturnApplicationDetail() {
        UUID applicationId = UUID.randomUUID();
        LoanApplicationDTO expected = new LoanApplicationDTO();

        when(loanApplicationsApi.getLoanApplication(applicationId, null))
                .thenReturn(Mono.just(expected));

        GetApplicationDetailQuery query = GetApplicationDetailQuery.builder()
                .applicationId(applicationId)
                .build();

        StepVerifier.create(handler.doHandle(query))
                .expectNext(expected)
                .verifyComplete();

        verify(loanApplicationsApi).getLoanApplication(applicationId, null);
    }
}
