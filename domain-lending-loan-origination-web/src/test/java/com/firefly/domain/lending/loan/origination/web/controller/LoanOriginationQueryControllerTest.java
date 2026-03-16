package com.firefly.domain.lending.loan.origination.web.controller;

import com.firefly.core.lending.origination.sdk.model.*;
import com.firefly.domain.lending.loan.origination.core.loan.origination.queries.*;
import org.fireflyframework.cqrs.query.QueryBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanOriginationQueryControllerTest {

    @Mock
    private QueryBus queryBus;

    @InjectMocks
    private LoanOriginationQueryController controller;

    private UUID applicationId;

    @BeforeEach
    void setUp() {
        applicationId = UUID.randomUUID();
    }

    @Test
    void getApplicationConditions_returnsOk() {
        var dto = new PaginationResponseApplicationConditionDTO();
        when(queryBus.query(any(GetApplicationConditionsQuery.class))).thenReturn(Mono.just(dto));

        StepVerifier.create(controller.getApplicationConditions(applicationId))
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.OK && response.getBody() == dto)
                .verifyComplete();

        verify(queryBus).query(any(GetApplicationConditionsQuery.class));
    }

    @Test
    void getApplicationConditions_returnsNotFound_whenEmpty() {
        when(queryBus.query(any(GetApplicationConditionsQuery.class))).thenReturn(Mono.empty());

        StepVerifier.create(controller.getApplicationConditions(applicationId))
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.NOT_FOUND)
                .verifyComplete();
    }

    @Test
    void getApplicationFees_returnsOk() {
        var dto = new PaginationResponseApplicationFeeDTO();
        when(queryBus.query(any(GetApplicationFeesQuery.class))).thenReturn(Mono.just(dto));

        StepVerifier.create(controller.getApplicationFees(applicationId))
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.OK && response.getBody() == dto)
                .verifyComplete();

        verify(queryBus).query(any(GetApplicationFeesQuery.class));
    }

    @Test
    void getApplicationFees_returnsNotFound_whenEmpty() {
        when(queryBus.query(any(GetApplicationFeesQuery.class))).thenReturn(Mono.empty());

        StepVerifier.create(controller.getApplicationFees(applicationId))
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.NOT_FOUND)
                .verifyComplete();
    }

    @Test
    void getApplicationTasks_returnsOk() {
        var dto = new PaginationResponseApplicationTaskDTO();
        when(queryBus.query(any(GetApplicationTasksQuery.class))).thenReturn(Mono.just(dto));

        StepVerifier.create(controller.getApplicationTasks(applicationId))
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.OK && response.getBody() == dto)
                .verifyComplete();

        verify(queryBus).query(any(GetApplicationTasksQuery.class));
    }

    @Test
    void getApplicationTasks_returnsNotFound_whenEmpty() {
        when(queryBus.query(any(GetApplicationTasksQuery.class))).thenReturn(Mono.empty());

        StepVerifier.create(controller.getApplicationTasks(applicationId))
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.NOT_FOUND)
                .verifyComplete();
    }

    @Test
    void getApplicationVerifications_returnsOk() {
        var dto = new PaginationResponseApplicationVerificationDTO();
        when(queryBus.query(any(GetApplicationVerificationsQuery.class))).thenReturn(Mono.just(dto));

        StepVerifier.create(controller.getApplicationVerifications(applicationId))
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.OK && response.getBody() == dto)
                .verifyComplete();

        verify(queryBus).query(any(GetApplicationVerificationsQuery.class));
    }

    @Test
    void getApplicationVerifications_returnsNotFound_whenEmpty() {
        when(queryBus.query(any(GetApplicationVerificationsQuery.class))).thenReturn(Mono.empty());

        StepVerifier.create(controller.getApplicationVerifications(applicationId))
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.NOT_FOUND)
                .verifyComplete();
    }

    @Test
    void getApplicationParties_returnsOk() {
        var dto = new PaginationResponseApplicationPartyDTO();
        when(queryBus.query(any(GetApplicationPartiesQuery.class))).thenReturn(Mono.just(dto));

        StepVerifier.create(controller.getApplicationParties(applicationId))
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.OK && response.getBody() == dto)
                .verifyComplete();

        verify(queryBus).query(any(GetApplicationPartiesQuery.class));
    }

    @Test
    void getApplicationParties_returnsNotFound_whenEmpty() {
        when(queryBus.query(any(GetApplicationPartiesQuery.class))).thenReturn(Mono.empty());

        StepVerifier.create(controller.getApplicationParties(applicationId))
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.NOT_FOUND)
                .verifyComplete();
    }

    @Test
    void getApplicationDocuments_returnsOk() {
        var dto = new PaginationResponseApplicationDocumentDTO();
        when(queryBus.query(any(GetApplicationDocumentsQuery.class))).thenReturn(Mono.just(dto));

        StepVerifier.create(controller.getApplicationDocuments(applicationId))
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.OK && response.getBody() == dto)
                .verifyComplete();

        verify(queryBus).query(any(GetApplicationDocumentsQuery.class));
    }

    @Test
    void getApplicationDocuments_returnsNotFound_whenEmpty() {
        when(queryBus.query(any(GetApplicationDocumentsQuery.class))).thenReturn(Mono.empty());

        StepVerifier.create(controller.getApplicationDocuments(applicationId))
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.NOT_FOUND)
                .verifyComplete();
    }

    @Test
    void getApplicationBankAccounts_returnsOk() {
        var dto = new PaginationResponseApplicationExternalBankAccountDTO();
        when(queryBus.query(any(GetApplicationBankAccountsQuery.class))).thenReturn(Mono.just(dto));

        StepVerifier.create(controller.getApplicationBankAccounts(applicationId))
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.OK && response.getBody() == dto)
                .verifyComplete();

        verify(queryBus).query(any(GetApplicationBankAccountsQuery.class));
    }

    @Test
    void getApplicationBankAccounts_returnsNotFound_whenEmpty() {
        when(queryBus.query(any(GetApplicationBankAccountsQuery.class))).thenReturn(Mono.empty());

        StepVerifier.create(controller.getApplicationBankAccounts(applicationId))
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.NOT_FOUND)
                .verifyComplete();
    }

    @Test
    void getApplicationDecisions_returnsOk() {
        var dto = new PaginationResponseUnderwritingDecisionDTO();
        when(queryBus.query(any(GetApplicationDecisionsQuery.class))).thenReturn(Mono.just(dto));

        StepVerifier.create(controller.getApplicationDecisions(applicationId))
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.OK && response.getBody() == dto)
                .verifyComplete();

        verify(queryBus).query(any(GetApplicationDecisionsQuery.class));
    }

    @Test
    void getApplicationDecisions_returnsNotFound_whenEmpty() {
        when(queryBus.query(any(GetApplicationDecisionsQuery.class))).thenReturn(Mono.empty());

        StepVerifier.create(controller.getApplicationDecisions(applicationId))
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.NOT_FOUND)
                .verifyComplete();
    }

    @Test
    void getApplicationOffers_returnsOk() {
        var dto = new PaginationResponseProposedOfferDTO();
        when(queryBus.query(any(GetApplicationOffersQuery.class))).thenReturn(Mono.just(dto));

        StepVerifier.create(controller.getApplicationOffers(applicationId))
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.OK && response.getBody() == dto)
                .verifyComplete();

        verify(queryBus).query(any(GetApplicationOffersQuery.class));
    }

    @Test
    void getApplicationOffers_returnsNotFound_whenEmpty() {
        when(queryBus.query(any(GetApplicationOffersQuery.class))).thenReturn(Mono.empty());

        StepVerifier.create(controller.getApplicationOffers(applicationId))
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.NOT_FOUND)
                .verifyComplete();
    }

    @Test
    void getApplicationDetail_returnsOk() {
        var dto = new LoanApplicationDTO();
        when(queryBus.query(any(GetApplicationDetailQuery.class))).thenReturn(Mono.just(dto));

        StepVerifier.create(controller.getApplicationDetail(applicationId))
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.OK && response.getBody() == dto)
                .verifyComplete();

        verify(queryBus).query(any(GetApplicationDetailQuery.class));
    }

    @Test
    void getApplicationDetail_returnsNotFound_whenEmpty() {
        when(queryBus.query(any(GetApplicationDetailQuery.class))).thenReturn(Mono.empty());

        StepVerifier.create(controller.getApplicationDetail(applicationId))
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.NOT_FOUND)
                .verifyComplete();
    }
}
