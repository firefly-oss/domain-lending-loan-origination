/*
 * Copyright 2026 Firefly Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.fireflysoftware.domain.lending.loan.origination.web.controller;

import io.fireflysoftware.core.lending.origination.sdk.model.ApplicationExternalBankAccountDTO;
import io.fireflysoftware.core.lending.origination.sdk.model.ApplicationPartyDTO;
import io.fireflysoftware.core.lending.origination.sdk.model.ApplicationTaskDTO;
import io.fireflysoftware.core.lending.origination.sdk.model.LoanApplicationDTO;
import io.fireflysoftware.core.lending.origination.sdk.model.ProposedOfferDTO;
import io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.services.OriginationRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Origination writes that persist through the loan origination core.
 */
@RestController
@RequestMapping("/api/v1/loan-origination/applications/{applicationId}")
@RequiredArgsConstructor
@Tag(name = "Loan Origination", description = "CQ queries and registration for Loan Origination")
public class LoanOriginationRecordController {

    private final OriginationRecordService originationRecordService;

    @Operation(operationId = "addApplicationParty", summary = "Add application party")
    @PostMapping("/parties")
    public Mono<ResponseEntity<ApplicationPartyDTO>> addApplicationParty(
            @PathVariable UUID applicationId,
            @RequestBody ApplicationPartyDTO party) {
        return originationRecordService.addParty(applicationId, party)
                .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved));
    }

    @Operation(operationId = "updateApplicationParty", summary = "Update application party")
    @PutMapping("/parties/{partyId}")
    public Mono<ResponseEntity<ApplicationPartyDTO>> updateApplicationParty(
            @PathVariable UUID applicationId,
            @PathVariable UUID partyId,
            @RequestBody ApplicationPartyDTO party) {
        return originationRecordService.updateParty(applicationId, partyId, party)
                .map(ResponseEntity::ok);
    }

    @Operation(operationId = "removeApplicationParty", summary = "Remove application party")
    @DeleteMapping("/parties/{partyId}")
    public Mono<ResponseEntity<Void>> removeApplicationParty(
            @PathVariable UUID applicationId,
            @PathVariable UUID partyId) {
        return originationRecordService.removeParty(applicationId, partyId)
                .thenReturn(ResponseEntity.noContent().build());
    }

    @Operation(operationId = "registerExternalBankAccount", summary = "Register external bank account")
    @PostMapping("/bank-accounts")
    public Mono<ResponseEntity<ApplicationExternalBankAccountDTO>> registerExternalBankAccount(
            @PathVariable UUID applicationId,
            @RequestBody ApplicationExternalBankAccountDTO account) {
        return originationRecordService.registerExternalBankAccount(applicationId, account)
                .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved));
    }

    @Operation(operationId = "deleteExternalBankAccount", summary = "Delete external bank account")
    @DeleteMapping("/bank-accounts/{accountId}")
    public Mono<ResponseEntity<Void>> deleteExternalBankAccount(
            @PathVariable UUID applicationId,
            @PathVariable UUID accountId) {
        return originationRecordService.deleteExternalBankAccount(applicationId, accountId)
                .thenReturn(ResponseEntity.noContent().build());
    }

    @Operation(operationId = "configureDisbursementAccount", summary = "Configure disbursement account")
    @PutMapping("/disbursement-account")
    public Mono<ResponseEntity<LoanApplicationDTO>> configureDisbursementAccount(
            @PathVariable UUID applicationId,
            @RequestBody LoanApplicationDTO disbursement) {
        return originationRecordService.configureDisbursementAccount(applicationId, disbursement)
                .map(ResponseEntity::ok);
    }

    @Operation(operationId = "completeApplicationTask", summary = "Complete application task")
    @PostMapping("/tasks/{taskId}/complete")
    public Mono<ResponseEntity<ApplicationTaskDTO>> completeApplicationTask(
            @PathVariable UUID applicationId,
            @PathVariable UUID taskId) {
        return originationRecordService.completeTask(applicationId, taskId)
                .map(ResponseEntity::ok);
    }

    @Operation(operationId = "deleteApplicationDocument", summary = "Delete application document")
    @DeleteMapping("/documents/{documentId}")
    public Mono<ResponseEntity<Void>> deleteApplicationDocument(
            @PathVariable UUID applicationId,
            @PathVariable UUID documentId) {
        return originationRecordService.deleteDocument(applicationId, documentId)
                .thenReturn(ResponseEntity.noContent().build());
    }

    @Operation(operationId = "updateApplicationOffer", summary = "Update application offer")
    @PutMapping("/offers/{offerId}")
    public Mono<ResponseEntity<ProposedOfferDTO>> updateApplicationOffer(
            @PathVariable UUID applicationId,
            @PathVariable UUID offerId,
            @RequestBody ProposedOfferDTO patch) {
        return originationRecordService.updateOffer(applicationId, offerId, patch)
                .map(ResponseEntity::ok);
    }
}
