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

package com.firefly.domain.lending.loan.origination.core.simulation.handlers;

import com.firefly.core.lending.origination.sdk.api.SimulationApi;
import com.firefly.core.lending.origination.sdk.model.SimulationDTO;
import com.firefly.domain.lending.loan.origination.core.simulation.commands.PersistSimulationCommand;
import com.firefly.domain.lending.loan.origination.core.util.IdempotencyKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Persists a pre-computed simulation by translating the
 * {@link PersistSimulationCommand} into a {@link SimulationDTO} and calling
 * the core lending loan-origination SDK.
 */
@Slf4j
@RequiredArgsConstructor
@CommandHandlerComponent
public class PersistSimulationCommandHandler
        extends CommandHandler<PersistSimulationCommand, SimulationDTO> {

    private final SimulationApi simulationApi;

    @Override
    protected Mono<SimulationDTO> doHandle(PersistSimulationCommand cmd) {
        SimulationDTO dto = new SimulationDTO()
                .productId(cmd.getProductId())
                .productType(cmd.getProductType())
                .requestedAmount(cmd.getRequestedAmount())
                .term(cmd.getTerm())
                .purpose(cmd.getPurpose())
                .sector(cmd.getSector())
                .assetType(cmd.getAssetType())
                .monthlyPayment(cmd.getMonthlyPayment())
                .tin(cmd.getTin())
                .tae(cmd.getTae())
                .totalAmount(cmd.getTotalAmount())
                .currency(cmd.getCurrency());

        // Simulations have no natural business key (a user can run the same
        // simulation many times intentionally). To prevent duplicate rows from
        // a *single* logical request that retries (network blip, handler retry)
        // we bucket by minute and hash all input fields: identical inputs within
        // the same minute collapse to the same key. Two genuinely distinct
        // simulation requests by the same user with identical inputs in the
        // same minute will be deduplicated -- this is an acceptable trade-off
        // for safety against duplicate writes during transient failures, and
        // mirrors how most payment gateways handle near-duplicate transactions.
        String minuteBucket = Instant.now().truncatedTo(ChronoUnit.MINUTES).toString();
        String idempotencyKey = IdempotencyKeys.of(
                "persist-simulation",
                Objects.toString(cmd.getProductId(), "null"),
                Objects.toString(cmd.getProductType(), "null"),
                Objects.toString(cmd.getRequestedAmount(), "null"),
                Objects.toString(cmd.getTerm(), "null"),
                Objects.toString(cmd.getPurpose(), "null"),
                Objects.toString(cmd.getSector(), "null"),
                Objects.toString(cmd.getAssetType(), "null"),
                Objects.toString(cmd.getCurrency(), "null"),
                minuteBucket);
        log.debug("Persisting simulation for productId={} term={} idempotencyKey={}",
                cmd.getProductId(), cmd.getTerm(), idempotencyKey);

        return simulationApi.createSimulation(dto, idempotencyKey);
    }
}
