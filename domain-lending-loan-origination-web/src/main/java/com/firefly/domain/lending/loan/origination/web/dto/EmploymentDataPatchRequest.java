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

package com.firefly.domain.lending.loan.origination.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request body for updating the economic / employment data of the primary
 * application party associated with a loan application. The {@code applicationId}
 * is taken from the path variable, so it is intentionally not part of this DTO.
 *
 * <p>The 12 economic fields here mirror the shape of the underlying core lending
 * loan-origination SDK {@code EmploymentDataPatchDTO} so the controller can map
 * them one-to-one onto the patch payload.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Patch payload with the economic / employment data of the primary application party.")
public class EmploymentDataPatchRequest {

    @Schema(description = "Employment status code (e.g. EMPLOYED, SELF_EMPLOYED, RETIRED).", example = "EMPLOYED")
    private String employmentStatus;

    @Schema(description = "Free-text employment type label.", example = "Permanent contract")
    private String employmentTypeLabel;

    @Schema(description = "Employer legal or commercial name.", example = "ACME Corp")
    private String employer;

    @Schema(description = "Job position or role.", example = "Software Engineer")
    private String position;

    @Schema(description = "Employment start date.", example = "2020-04-15")
    private LocalDate employmentStartDate;

    @Schema(description = "Number of paydays per year.", example = "14")
    private Short annualPaydays;

    @Schema(description = "Net monthly salary.", example = "2500.00")
    private BigDecimal monthlySalary;

    @Schema(description = "Housing tenure type (e.g. OWNED, RENTED, MORTGAGED).", example = "RENTED")
    private String housingType;

    @Schema(description = "Monthly housing cost (rent or mortgage payment).", example = "850.00")
    private BigDecimal housingCost;

    @Schema(description = "Date the current housing arrangement started.", example = "2018-09-01")
    private LocalDate housingStartDate;

    @Schema(description = "Number of currently active loans of the applicant.", example = "1")
    private Short existingLoans;

    @Schema(description = "Total monthly payment of other debts (excluding housing).", example = "150.00")
    private BigDecimal otherDebts;
}
