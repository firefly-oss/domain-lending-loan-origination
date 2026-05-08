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

package com.firefly.domain.lending.loan.origination.core.applicationparty.commands;

import com.firefly.core.lending.origination.sdk.model.ApplicationPartyDTO;
import com.firefly.core.lending.origination.sdk.model.EmploymentDataPatchDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fireflyframework.cqrs.command.Command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Command that updates the economic / employment data of the primary
 * {@link ApplicationPartyDTO} associated with a loan application.
 *
 * <p>The 12 economic fields here mirror the shape of
 * {@link EmploymentDataPatchDTO} exposed by the core lending loan-origination
 * SDK so the handler can map them one-to-one onto the patch payload.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateApplicationEmploymentDataCommand implements Command<ApplicationPartyDTO> {

    private UUID applicationId;

    private String employmentStatus;
    private String employmentTypeLabel;
    private String employer;
    private String position;
    private LocalDate employmentStartDate;
    private Short annualPaydays;
    private BigDecimal monthlySalary;
    private String housingType;
    private BigDecimal housingCost;
    private LocalDate housingStartDate;
    private Short existingLoans;
    private BigDecimal otherDebts;
}
