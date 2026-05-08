package com.firefly.domain.lending.loan.origination.core.simulation.handlers;

import com.firefly.core.lending.origination.sdk.api.SimulationApi;
import com.firefly.core.lending.origination.sdk.model.SimulationDTO;
import com.firefly.domain.lending.loan.origination.core.simulation.commands.PersistSimulationCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersistSimulationCommandHandlerTest {

    @Mock
    private SimulationApi simulationApi;

    @InjectMocks
    private PersistSimulationCommandHandler handler;

    @Test
    void doHandle_shouldMapCommandFieldsOntoSimulationDTOAndUseIdempotencyKey() {
        UUID productId = UUID.randomUUID();
        PersistSimulationCommand cmd = PersistSimulationCommand.builder()
                .productId(productId)
                .productType("LOAN")
                .requestedAmount(new BigDecimal("12000.00"))
                .term(48)
                .purpose("HOME_REFURBISHMENT")
                .sector("CONSUMER")
                .assetType("UNSECURED")
                .monthlyPayment(new BigDecimal("289.50"))
                .tin(new BigDecimal("0.0599"))
                .tae(new BigDecimal("0.0625"))
                .totalAmount(new BigDecimal("13896.00"))
                .currency("EUR")
                .build();

        SimulationDTO persisted = new SimulationDTO()
                .productId(productId)
                .requestedAmount(cmd.getRequestedAmount());

        ArgumentCaptor<SimulationDTO> dtoCaptor = ArgumentCaptor.forClass(SimulationDTO.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);

        when(simulationApi.createSimulation(any(SimulationDTO.class), anyString()))
                .thenReturn(Mono.just(persisted));

        StepVerifier.create(handler.doHandle(cmd))
                .expectNext(persisted)
                .verifyComplete();

        verify(simulationApi).createSimulation(dtoCaptor.capture(), keyCaptor.capture());

        SimulationDTO sent = dtoCaptor.getValue();
        assertThat(sent.getProductId()).isEqualTo(productId);
        assertThat(sent.getProductType()).isEqualTo("LOAN");
        assertThat(sent.getRequestedAmount()).isEqualByComparingTo("12000.00");
        assertThat(sent.getTerm()).isEqualTo(48);
        assertThat(sent.getPurpose()).isEqualTo("HOME_REFURBISHMENT");
        assertThat(sent.getSector()).isEqualTo("CONSUMER");
        assertThat(sent.getAssetType()).isEqualTo("UNSECURED");
        assertThat(sent.getMonthlyPayment()).isEqualByComparingTo("289.50");
        assertThat(sent.getTin()).isEqualByComparingTo("0.0599");
        assertThat(sent.getTae()).isEqualByComparingTo("0.0625");
        assertThat(sent.getTotalAmount()).isEqualByComparingTo("13896.00");
        assertThat(sent.getCurrency()).isEqualTo("EUR");

        assertThat(keyCaptor.getValue()).isNotNull().isNotBlank();
        // Sanity: the idempotency key must be a valid UUID string.
        assertThat(UUID.fromString(keyCaptor.getValue())).isNotNull();
    }
}
