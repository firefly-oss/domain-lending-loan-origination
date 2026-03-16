package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.core.lending.origination.sdk.api.ApplicationExternalBankAccountsApi;
import com.firefly.core.lending.origination.sdk.model.PaginationResponseApplicationExternalBankAccountDTO;
import com.firefly.domain.lending.loan.origination.core.loan.origination.queries.GetApplicationBankAccountsQuery;
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
class GetApplicationBankAccountsHandlerTest {

    @Mock
    private ApplicationExternalBankAccountsApi applicationExternalBankAccountsApi;

    @InjectMocks
    private GetApplicationBankAccountsHandler handler;

    @Test
    void doHandle_shouldReturnBankAccounts() {
        UUID applicationId = UUID.randomUUID();
        PaginationResponseApplicationExternalBankAccountDTO expected = new PaginationResponseApplicationExternalBankAccountDTO();

        when(applicationExternalBankAccountsApi.findAll(applicationId, null, null))
                .thenReturn(Mono.just(expected));

        GetApplicationBankAccountsQuery query = GetApplicationBankAccountsQuery.builder()
                .applicationId(applicationId)
                .build();

        StepVerifier.create(handler.doHandle(query))
                .expectNext(expected)
                .verifyComplete();

        verify(applicationExternalBankAccountsApi).findAll(applicationId, null, null);
    }
}
