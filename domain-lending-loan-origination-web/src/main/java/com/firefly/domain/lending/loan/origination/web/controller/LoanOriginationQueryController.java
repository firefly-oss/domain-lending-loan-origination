package com.firefly.domain.lending.loan.origination.web.controller;

import com.firefly.core.lending.origination.sdk.model.*;
import com.firefly.domain.lending.loan.origination.core.loan.origination.queries.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.query.QueryBus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/loan-origination/applications")
@RequiredArgsConstructor
@Tag(name = "Loan Origination", description = "CQ queries and registration for Loan Origination")
public class LoanOriginationQueryController {

    private final QueryBus queryBus;

    @Operation(summary = "Get application conditions")
    @ApiResponse(responseCode = "200", description = "Application conditions retrieved successfully")
    @GetMapping("/{applicationId}/conditions")
    public Mono<ResponseEntity<PaginationResponseApplicationConditionDTO>> getApplicationConditions(
            @PathVariable UUID applicationId) {
        log.info("Fetching conditions for application: {}", applicationId);
        return queryBus.query(GetApplicationConditionsQuery.builder().applicationId(applicationId).build())
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @Operation(summary = "Get application fees")
    @ApiResponse(responseCode = "200", description = "Application fees retrieved successfully")
    @GetMapping("/{applicationId}/fees")
    public Mono<ResponseEntity<PaginationResponseApplicationFeeDTO>> getApplicationFees(
            @PathVariable UUID applicationId) {
        log.info("Fetching fees for application: {}", applicationId);
        return queryBus.query(GetApplicationFeesQuery.builder().applicationId(applicationId).build())
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @Operation(summary = "Get application tasks")
    @ApiResponse(responseCode = "200", description = "Application tasks retrieved successfully")
    @GetMapping("/{applicationId}/tasks")
    public Mono<ResponseEntity<PaginationResponseApplicationTaskDTO>> getApplicationTasks(
            @PathVariable UUID applicationId) {
        log.info("Fetching tasks for application: {}", applicationId);
        return queryBus.query(GetApplicationTasksQuery.builder().applicationId(applicationId).build())
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @Operation(summary = "Get application verifications")
    @ApiResponse(responseCode = "200", description = "Application verifications retrieved successfully")
    @GetMapping("/{applicationId}/verifications")
    public Mono<ResponseEntity<PaginationResponseApplicationVerificationDTO>> getApplicationVerifications(
            @PathVariable UUID applicationId) {
        log.info("Fetching verifications for application: {}", applicationId);
        return queryBus.query(GetApplicationVerificationsQuery.builder().applicationId(applicationId).build())
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @Operation(summary = "Get application parties")
    @ApiResponse(responseCode = "200", description = "Application parties retrieved successfully")
    @GetMapping("/{applicationId}/parties")
    public Mono<ResponseEntity<PaginationResponseApplicationPartyDTO>> getApplicationParties(
            @PathVariable UUID applicationId) {
        log.info("Fetching parties for application: {}", applicationId);
        return queryBus.query(GetApplicationPartiesQuery.builder().applicationId(applicationId).build())
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @Operation(summary = "Get application documents")
    @ApiResponse(responseCode = "200", description = "Application documents retrieved successfully")
    @GetMapping("/{applicationId}/documents")
    public Mono<ResponseEntity<PaginationResponseApplicationDocumentDTO>> getApplicationDocuments(
            @PathVariable UUID applicationId) {
        log.info("Fetching documents for application: {}", applicationId);
        return queryBus.query(GetApplicationDocumentsQuery.builder().applicationId(applicationId).build())
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @Operation(summary = "Get application bank accounts")
    @ApiResponse(responseCode = "200", description = "Application bank accounts retrieved successfully")
    @GetMapping("/{applicationId}/bank-accounts")
    public Mono<ResponseEntity<PaginationResponseApplicationExternalBankAccountDTO>> getApplicationBankAccounts(
            @PathVariable UUID applicationId) {
        log.info("Fetching bank accounts for application: {}", applicationId);
        return queryBus.query(GetApplicationBankAccountsQuery.builder().applicationId(applicationId).build())
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @Operation(summary = "Get application decisions")
    @ApiResponse(responseCode = "200", description = "Application decisions retrieved successfully")
    @GetMapping("/{applicationId}/decisions")
    public Mono<ResponseEntity<PaginationResponseUnderwritingDecisionDTO>> getApplicationDecisions(
            @PathVariable UUID applicationId) {
        log.info("Fetching decisions for application: {}", applicationId);
        return queryBus.query(GetApplicationDecisionsQuery.builder().applicationId(applicationId).build())
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @Operation(summary = "Get application offers")
    @ApiResponse(responseCode = "200", description = "Application offers retrieved successfully")
    @GetMapping("/{applicationId}/offers")
    public Mono<ResponseEntity<PaginationResponseProposedOfferDTO>> getApplicationOffers(
            @PathVariable UUID applicationId) {
        log.info("Fetching offers for application: {}", applicationId);
        return queryBus.query(GetApplicationOffersQuery.builder().applicationId(applicationId).build())
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @Operation(summary = "Get application detail")
    @ApiResponse(responseCode = "200", description = "Application detail retrieved successfully")
    @GetMapping("/{applicationId}/detail")
    public Mono<ResponseEntity<LoanApplicationDTO>> getApplicationDetail(
            @PathVariable UUID applicationId) {
        log.info("Fetching detail for application: {}", applicationId);
        return queryBus.query(GetApplicationDetailQuery.builder().applicationId(applicationId).build())
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }
}
