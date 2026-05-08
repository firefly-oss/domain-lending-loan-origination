package com.firefly.domain.lending.loan.origination.core.applicationparty.handlers;

import com.firefly.core.lending.origination.sdk.api.ApplicationPartyEmploymentApi;
import com.firefly.core.lending.origination.sdk.model.ApplicationPartyDTO;
import com.firefly.core.lending.origination.sdk.model.EmploymentDataPatchDTO;
import com.firefly.domain.lending.loan.origination.core.applicationparty.commands.UpdateApplicationEmploymentDataCommand;
import org.fireflyframework.web.error.exceptions.BusinessException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateApplicationEmploymentDataCommandHandlerTest {

    @Mock
    private ApplicationPartyEmploymentApi applicationPartyEmploymentApi;

    @InjectMocks
    private UpdateApplicationEmploymentDataCommandHandler handler;

    @Test
    void doHandle_shouldResolvePrimaryPartyAndPatchAll12Fields() {
        UUID applicationId = UUID.randomUUID();
        UUID primaryPartyId = UUID.randomUUID();

        UpdateApplicationEmploymentDataCommand cmd = UpdateApplicationEmploymentDataCommand.builder()
                .applicationId(applicationId)
                .employmentStatus("EMPLOYED")
                .employmentTypeLabel("PERMANENT")
                .employer("Acme Corp")
                .position("Senior Engineer")
                .employmentStartDate(LocalDate.of(2020, 6, 1))
                .annualPaydays((short) 14)
                .monthlySalary(new BigDecimal("3500.00"))
                .housingType("MORTGAGE")
                .housingCost(new BigDecimal("950.00"))
                .housingStartDate(LocalDate.of(2018, 9, 1))
                .existingLoans((short) 1)
                .otherDebts(new BigDecimal("250.00"))
                .build();

        ApplicationPartyDTO primary = new ApplicationPartyDTO(primaryPartyId);
        ApplicationPartyDTO updated = new ApplicationPartyDTO(primaryPartyId);
        updated.setEmployer("Acme Corp");

        when(applicationPartyEmploymentApi.findPrimaryApplicationParty(eq(applicationId), anyString()))
                .thenReturn(Mono.just(primary));
        when(applicationPartyEmploymentApi.updateApplicationPartyEmploymentData(
                eq(primaryPartyId), any(EmploymentDataPatchDTO.class), anyString()))
                .thenReturn(Mono.just(updated));

        StepVerifier.create(handler.doHandle(cmd))
                .expectNext(updated)
                .verifyComplete();

        ArgumentCaptor<EmploymentDataPatchDTO> patchCaptor =
                ArgumentCaptor.forClass(EmploymentDataPatchDTO.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> findKeyCaptor = ArgumentCaptor.forClass(String.class);

        verify(applicationPartyEmploymentApi)
                .findPrimaryApplicationParty(eq(applicationId), findKeyCaptor.capture());
        verify(applicationPartyEmploymentApi).updateApplicationPartyEmploymentData(
                eq(primaryPartyId), patchCaptor.capture(), keyCaptor.capture());

        EmploymentDataPatchDTO patch = patchCaptor.getValue();
        assertThat(patch.getEmploymentStatus()).isEqualTo("EMPLOYED");
        assertThat(patch.getEmploymentTypeLabel()).isEqualTo("PERMANENT");
        assertThat(patch.getEmployer()).isEqualTo("Acme Corp");
        assertThat(patch.getPosition()).isEqualTo("Senior Engineer");
        assertThat(patch.getEmploymentStartDate()).isEqualTo(LocalDate.of(2020, 6, 1));
        assertThat(patch.getAnnualPaydays()).isEqualTo(14);
        assertThat(patch.getMonthlySalary()).isEqualByComparingTo("3500.00");
        assertThat(patch.getHousingType()).isEqualTo("MORTGAGE");
        assertThat(patch.getHousingCost()).isEqualByComparingTo("950.00");
        assertThat(patch.getHousingStartDate()).isEqualTo(LocalDate.of(2018, 9, 1));
        assertThat(patch.getExistingLoans()).isEqualTo(1);
        assertThat(patch.getOtherDebts()).isEqualByComparingTo("250.00");

        // Both calls must use a non-null UUID-shaped idempotency key, and they must differ.
        assertThat(findKeyCaptor.getValue()).isNotNull().isNotBlank();
        assertThat(keyCaptor.getValue()).isNotNull().isNotBlank();
        assertThat(UUID.fromString(findKeyCaptor.getValue())).isNotNull();
        assertThat(UUID.fromString(keyCaptor.getValue())).isNotNull();
        assertThat(findKeyCaptor.getValue()).isNotEqualTo(keyCaptor.getValue());
    }

    @Test
    void doHandle_shouldErrorWhenNoPrimaryPartyFound() {
        UUID applicationId = UUID.randomUUID();

        UpdateApplicationEmploymentDataCommand cmd = UpdateApplicationEmploymentDataCommand.builder()
                .applicationId(applicationId)
                .employmentStatus("EMPLOYED")
                .build();

        when(applicationPartyEmploymentApi.findPrimaryApplicationParty(eq(applicationId), anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(handler.doHandle(cmd))
                .expectErrorSatisfies(err -> {
                    assertThat(err).isInstanceOf(BusinessException.class);
                    BusinessException be = (BusinessException) err;
                    assertThat(be.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(be.getCode()).isEqualTo("PRIMARY_APPLICATION_PARTY_NOT_FOUND");
                    assertThat(be.getMessage()).contains(applicationId.toString());
                })
                .verify();
    }
}
