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

package com.firefly.domain.lending.loan.origination.core.simulation.commands;

import com.firefly.core.lending.origination.sdk.model.SimulationDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fireflyframework.cqrs.command.Command;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Command to persist a pre-computed loan simulation through the core lending
 * loan-origination service.
 *
 * <p>The handler maps these fields onto a {@link SimulationDTO} and invokes
 * the SDK's {@code SimulationApi.createSimulation} endpoint with a generated
 * idempotency key.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersistSimulationCommand implements Command<SimulationDTO> {

    private UUID productId;
    private String productType;
    private BigDecimal requestedAmount;
    private Integer term;
    private String purpose;
    private String sector;
    private String assetType;
    private BigDecimal monthlyPayment;
    private BigDecimal tin;
    private BigDecimal tae;
    private BigDecimal totalAmount;
    private String currency;
}
