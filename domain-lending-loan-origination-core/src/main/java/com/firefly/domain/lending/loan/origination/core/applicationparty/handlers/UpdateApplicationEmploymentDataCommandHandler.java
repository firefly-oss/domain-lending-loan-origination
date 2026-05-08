/*
 * Copyright 2025 Firefly Software Foundation
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

package com.firefly.domain.lending.loan.origination.core.applicationparty.handlers;

import com.firefly.core.lending.origination.sdk.api.ApplicationPartyEmploymentApi;
import com.firefly.core.lending.origination.sdk.model.ApplicationPartyDTO;
import com.firefly.core.lending.origination.sdk.model.EmploymentDataPatchDTO;
import com.firefly.domain.lending.loan.origination.core.applicationparty.commands.UpdateApplicationEmploymentDataCommand;
import com.firefly.domain.lending.loan.origination.core.util.IdempotencyKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import org.fireflyframework.web.error.exceptions.BusinessException;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Updates the employment data of the primary application party of a given
 * loan application.
 *
 * <p>The handler resolves the primary application party first, then applies
 * the patch through the core lending loan-origination SDK using a freshly
 * generated idempotency key for every mutating call.</p>
 */
@Slf4j
@RequiredArgsConstructor
@CommandHandlerComponent
public class UpdateApplicationEmploymentDataCommandHandler
        extends CommandHandler<UpdateApplicationEmploymentDataCommand, ApplicationPartyDTO> {

    private final ApplicationPartyEmploymentApi applicationPartyEmploymentApi;

    @Override
    protected Mono<ApplicationPartyDTO> doHandle(UpdateApplicationEmploymentDataCommand cmd) {
        UUID applicationId = cmd.getApplicationId();
        log.debug("Resolving primary application party for applicationId={}", applicationId);

        // Read-only lookup: still pass a deterministic key so the downstream
        // service can deduplicate concurrent retries.
        String findKey = IdempotencyKeys.of(
                "find-primary-application-party", applicationId.toString());

        return applicationPartyEmploymentApi
                .findPrimaryApplicationParty(applicationId, findKey)
                .switchIfEmpty(Mono.error(new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "PRIMARY_APPLICATION_PARTY_NOT_FOUND",
                        "No primary application party found for applicationId=" + applicationId)))
                .onErrorMap(ex -> !(ex instanceof BusinessException),
                        ex -> new BusinessException(
                                HttpStatus.NOT_FOUND,
                                "PRIMARY_APPLICATION_PARTY_NOT_FOUND",
                                "Failed to resolve primary application party for applicationId="
                                        + applicationId, ex))
                .flatMap(primary -> {
                    UUID partyId = primary.getApplicationPartyId();
                    if (partyId == null) {
                        return Mono.error(new BusinessException(
                                HttpStatus.UNPROCESSABLE_ENTITY,
                                "PRIMARY_APPLICATION_PARTY_INVALID",
                                "Primary application party has no applicationPartyId for applicationId="
                                        + applicationId));
                    }

                    EmploymentDataPatchDTO patch = buildPatch(cmd);
                    // Deterministic key: same applicationId + same primary
                    // applicationPartyId must yield the same key so that retries
                    // (this handler is invoked under @CommandHandlerComponent
                    // and may be re-driven by upstream sagas) produce idempotent
                    // upserts on the employment-data row.
                    String idempotencyKey = IdempotencyKeys.of(
                            "update-application-employment",
                            applicationId.toString(),
                            partyId.toString());
                    log.debug("Updating employment data for applicationPartyId={} idempotencyKey={}",
                            partyId, idempotencyKey);

                    return applicationPartyEmploymentApi
                            .updateApplicationPartyEmploymentData(partyId, patch, idempotencyKey);
                });
    }

    private EmploymentDataPatchDTO buildPatch(UpdateApplicationEmploymentDataCommand cmd) {
        return new EmploymentDataPatchDTO()
                .employmentStatus(cmd.getEmploymentStatus())
                .employmentTypeLabel(cmd.getEmploymentTypeLabel())
                .employer(cmd.getEmployer())
                .position(cmd.getPosition())
                .employmentStartDate(cmd.getEmploymentStartDate())
                .annualPaydays(toInteger(cmd.getAnnualPaydays()))
                .monthlySalary(cmd.getMonthlySalary())
                .housingType(cmd.getHousingType())
                .housingCost(cmd.getHousingCost())
                .housingStartDate(cmd.getHousingStartDate())
                .existingLoans(toInteger(cmd.getExistingLoans()))
                .otherDebts(cmd.getOtherDebts());
    }

    private static Integer toInteger(Short value) {
        return value == null ? null : value.intValue();
    }
}
