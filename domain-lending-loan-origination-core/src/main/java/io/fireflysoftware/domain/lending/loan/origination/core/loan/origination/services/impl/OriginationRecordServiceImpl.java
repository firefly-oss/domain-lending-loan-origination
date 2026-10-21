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

package io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.services.impl;

import io.fireflysoftware.core.lending.origination.sdk.api.ApplicationDocumentApi;
import io.fireflysoftware.core.lending.origination.sdk.api.ApplicationExternalBankAccountsApi;
import io.fireflysoftware.core.lending.origination.sdk.api.ApplicationPartyApi;
import io.fireflysoftware.core.lending.origination.sdk.api.ApplicationTaskApi;
import io.fireflysoftware.core.lending.origination.sdk.api.LoanApplicationsApi;
import io.fireflysoftware.core.lending.origination.sdk.api.ProposedOfferApi;
import io.fireflysoftware.core.lending.origination.sdk.model.ApplicationExternalBankAccountDTO;
import io.fireflysoftware.core.lending.origination.sdk.model.ApplicationPartyDTO;
import io.fireflysoftware.core.lending.origination.sdk.model.ApplicationTaskDTO;
import io.fireflysoftware.core.lending.origination.sdk.model.LoanApplicationDTO;
import io.fireflysoftware.core.lending.origination.sdk.model.ProposedOfferDTO;
import io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.services.OriginationRecordService;
import io.fireflysoftware.domain.lending.loan.origination.core.shared.CustomerContractLink;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Delegates origination writes to the core SDK methods that already persist them.
 */
@Service
public class OriginationRecordServiceImpl implements OriginationRecordService {

    private final ApplicationPartyApi applicationPartyApi;
    private final ApplicationExternalBankAccountsApi bankAccountsApi;
    private final ApplicationTaskApi applicationTaskApi;
    private final ApplicationDocumentApi applicationDocumentApi;
    private final ProposedOfferApi proposedOfferApi;
    private final LoanApplicationsApi loanApplicationsApi;
    private final CustomerContractLink customerContract;

    public OriginationRecordServiceImpl(
            ApplicationPartyApi applicationPartyApi,
            ApplicationExternalBankAccountsApi bankAccountsApi,
            ApplicationTaskApi applicationTaskApi,
            ApplicationDocumentApi applicationDocumentApi,
            ProposedOfferApi proposedOfferApi,
            LoanApplicationsApi loanApplicationsApi,
            CustomerContractLink customerContract) {
        this.applicationPartyApi = applicationPartyApi;
        this.bankAccountsApi = bankAccountsApi;
        this.applicationTaskApi = applicationTaskApi;
        this.applicationDocumentApi = applicationDocumentApi;
        this.proposedOfferApi = proposedOfferApi;
        this.loanApplicationsApi = loanApplicationsApi;
        this.customerContract = customerContract;
    }

    @Override
    public Mono<ApplicationPartyDTO> addParty(UUID applicationId, ApplicationPartyDTO party) {
        party.loanApplicationId(applicationId);
        return customerContract.bind(party.getPartyId(), null, null, null, party.getRoleCodeId())
                .then(applicationPartyApi.createParty(applicationId, party, key()));
    }

    @Override
    public Mono<ApplicationPartyDTO> updateParty(UUID applicationId, UUID partyId, ApplicationPartyDTO party) {
        party.loanApplicationId(applicationId);
        return applicationPartyApi.updateParty(applicationId, partyId, party, key());
    }

    @Override
    public Mono<Void> removeParty(UUID applicationId, UUID partyId) {
        return applicationPartyApi.deleteParty(applicationId, partyId, key());
    }

    @Override
    public Mono<ApplicationExternalBankAccountDTO> registerExternalBankAccount(
            UUID applicationId, ApplicationExternalBankAccountDTO account) {
        account.loanApplicationId(applicationId);
        return bankAccountsApi.create(applicationId, account, key());
    }

    @Override
    public Mono<Void> deleteExternalBankAccount(UUID applicationId, UUID accountId) {
        return bankAccountsApi.delete(applicationId, accountId, key());
    }

    @Override
    public Mono<LoanApplicationDTO> configureDisbursementAccount(UUID applicationId, LoanApplicationDTO disbursement) {
        return loanApplicationsApi.getLoanApplication(applicationId, key())
                .flatMap(current -> {
                    current.setDisbursementMethodType(disbursement.getDisbursementMethodType());
                    current.setDisbursementInternalAccountId(disbursement.getDisbursementInternalAccountId());
                    current.setDisbursementExternalBankAccountId(disbursement.getDisbursementExternalBankAccountId());
                    return loanApplicationsApi.updateLoanApplication(applicationId, current, key());
                });
    }

    @Override
    public Mono<ApplicationTaskDTO> completeTask(UUID applicationId, UUID taskId) {
        return applicationTaskApi.getTask(applicationId, taskId, key())
                .flatMap(task -> applicationTaskApi.updateTask(
                        applicationId,
                        taskId,
                        task.taskStatus("COMPLETED").completedAt(LocalDateTime.now()),
                        key()));
    }

    @Override
    public Mono<Void> deleteDocument(UUID applicationId, UUID documentId) {
        return applicationDocumentApi.deleteDocument(applicationId, documentId, key());
    }

    @Override
    public Mono<ProposedOfferDTO> updateOffer(UUID applicationId, UUID offerId, ProposedOfferDTO patch) {
        return proposedOfferApi.getOffer(applicationId, offerId, key())
                .flatMap(current -> {
                    if (patch.getOfferStatus() != null) {
                        current.offerStatus(patch.getOfferStatus());
                    }
                    if (patch.getRejectionReason() != null) {
                        current.rejectionReason(patch.getRejectionReason());
                    }
                    if (patch.getAcceptedAt() != null) {
                        current.acceptedAt(patch.getAcceptedAt());
                    }
                    return proposedOfferApi.updateOffer(applicationId, offerId, current, key());
                });
    }

    private static String key() {
        return UUID.randomUUID().toString();
    }
}
