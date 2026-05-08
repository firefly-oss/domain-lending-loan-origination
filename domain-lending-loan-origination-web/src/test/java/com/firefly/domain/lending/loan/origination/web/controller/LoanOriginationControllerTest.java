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

package com.firefly.domain.lending.loan.origination.web.controller;

import com.firefly.core.lending.origination.sdk.model.ApplicationPartyDTO;
import com.firefly.core.lending.origination.sdk.model.SimulationDTO;
import com.firefly.domain.lending.loan.origination.core.applicationparty.commands.UpdateApplicationEmploymentDataCommand;
import com.firefly.domain.lending.loan.origination.core.loan.origination.services.LoanOriginationService;
import com.firefly.domain.lending.loan.origination.core.simulation.commands.PersistSimulationCommand;
import com.firefly.domain.lending.loan.origination.web.dto.EmploymentDataPatchRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanOriginationControllerTest {

    @Mock
    private LoanOriginationService loanOriginationService;

    @InjectMocks
    private LoanOriginationController controller;

    private UUID applicationId;

    @BeforeEach
    void setUp() {
        applicationId = UUID.randomUUID();
    }

    // -----------------------------------------------------------------------
    // persistLendingSimulation
    // -----------------------------------------------------------------------

    @Test
    void persistLendingSimulation_returnsCreated() {
        PersistSimulationCommand cmd = PersistSimulationCommand.builder()
                .productId(UUID.randomUUID())
                .productType("PERSONAL_LOAN")
                .requestedAmount(new BigDecimal("10000.00"))
                .term(36)
                .currency("EUR")
                .monthlyPayment(new BigDecimal("310.00"))
                .tin(new BigDecimal("0.0599"))
                .tae(new BigDecimal("0.0625"))
                .totalAmount(new BigDecimal("11160.00"))
                .build();

        SimulationDTO dto = new SimulationDTO();
        when(loanOriginationService.persistSimulation(any(PersistSimulationCommand.class)))
                .thenReturn(Mono.just(dto));

        StepVerifier.create(controller.persistLendingSimulation(cmd))
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.CREATED
                        && response.getBody() == dto)
                .verifyComplete();

        verify(loanOriginationService).persistSimulation(any(PersistSimulationCommand.class));
    }

    @Test
    void persistLendingSimulation_propagatesServiceError() {
        PersistSimulationCommand cmd = PersistSimulationCommand.builder()
                .productType("PERSONAL_LOAN")
                .requestedAmount(new BigDecimal("10000.00"))
                .term(36)
                .currency("EUR")
                .build();

        RuntimeException expected = new RuntimeException("downstream failure");
        when(loanOriginationService.persistSimulation(any(PersistSimulationCommand.class)))
                .thenReturn(Mono.error(expected));

        StepVerifier.create(controller.persistLendingSimulation(cmd))
                .expectErrorMatches(ex -> ex == expected)
                .verify();
    }

    // -----------------------------------------------------------------------
    // updateApplicationEmploymentData
    // -----------------------------------------------------------------------

    @Test
    void updateApplicationEmploymentData_returnsOkAndMapsBodyOntoCommand() {
        EmploymentDataPatchRequest request = EmploymentDataPatchRequest.builder()
                .employmentStatus("EMPLOYED")
                .employmentTypeLabel("Permanent contract")
                .employer("ACME Corp")
                .position("Software Engineer")
                .employmentStartDate(LocalDate.of(2020, 4, 15))
                .annualPaydays((short) 14)
                .monthlySalary(new BigDecimal("2500.00"))
                .housingType("RENTED")
                .housingCost(new BigDecimal("850.00"))
                .housingStartDate(LocalDate.of(2018, 9, 1))
                .existingLoans((short) 1)
                .otherDebts(new BigDecimal("150.00"))
                .build();

        ApplicationPartyDTO dto = new ApplicationPartyDTO();
        when(loanOriginationService.updateApplicationEmploymentData(
                any(UpdateApplicationEmploymentDataCommand.class)))
                .thenReturn(Mono.just(dto));

        StepVerifier.create(controller.updateApplicationEmploymentData(applicationId, request))
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.OK
                        && response.getBody() == dto)
                .verifyComplete();

        ArgumentCaptor<UpdateApplicationEmploymentDataCommand> captor =
                ArgumentCaptor.forClass(UpdateApplicationEmploymentDataCommand.class);
        verify(loanOriginationService).updateApplicationEmploymentData(captor.capture());
        UpdateApplicationEmploymentDataCommand actual = captor.getValue();
        assertEquals(applicationId, actual.getApplicationId());
        assertEquals("EMPLOYED", actual.getEmploymentStatus());
        assertEquals("Permanent contract", actual.getEmploymentTypeLabel());
        assertEquals("ACME Corp", actual.getEmployer());
        assertEquals("Software Engineer", actual.getPosition());
        assertEquals(LocalDate.of(2020, 4, 15), actual.getEmploymentStartDate());
        assertEquals(Short.valueOf((short) 14), actual.getAnnualPaydays());
        assertEquals(new BigDecimal("2500.00"), actual.getMonthlySalary());
        assertEquals("RENTED", actual.getHousingType());
        assertEquals(new BigDecimal("850.00"), actual.getHousingCost());
        assertEquals(LocalDate.of(2018, 9, 1), actual.getHousingStartDate());
        assertEquals(Short.valueOf((short) 1), actual.getExistingLoans());
        assertEquals(new BigDecimal("150.00"), actual.getOtherDebts());
    }

    @Test
    void updateApplicationEmploymentData_propagatesServiceError() {
        EmploymentDataPatchRequest request = EmploymentDataPatchRequest.builder()
                .employmentStatus("EMPLOYED")
                .build();

        IllegalStateException expected = new IllegalStateException(
                "No primary application party found for applicationId=" + applicationId);
        when(loanOriginationService.updateApplicationEmploymentData(
                any(UpdateApplicationEmploymentDataCommand.class)))
                .thenReturn(Mono.error(expected));

        StepVerifier.create(controller.updateApplicationEmploymentData(applicationId, request))
                .expectErrorMatches(ex -> ex == expected)
                .verify();
    }
}
