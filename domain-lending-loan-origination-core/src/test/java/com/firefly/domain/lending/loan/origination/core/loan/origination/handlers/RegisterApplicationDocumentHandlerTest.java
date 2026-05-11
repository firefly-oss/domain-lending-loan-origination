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

package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import com.firefly.core.lending.origination.sdk.api.ApplicationDocumentApi;
import com.firefly.core.lending.origination.sdk.api.DocumentTypeApi;
import com.firefly.core.lending.origination.sdk.model.ApplicationDocumentDTO;
import com.firefly.core.lending.origination.sdk.model.DocumentType;
import com.firefly.domain.lending.loan.origination.core.loan.origination.commands.RegisterApplicationDocumentCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static com.firefly.domain.lending.loan.origination.core.loan.utils.constants.DocumentTypeDefaults.DEFAULT_DOCUMENT_TYPE_OTHER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link RegisterApplicationDocumentHandler}'s default-injection
 * and documentTypeId-resolution behaviour.
 *
 * <p>The core service's {@code ApplicationDocumentDTO} marks
 * {@code documentId}, {@code documentTypeId}, {@code isMandatory}, and
 * {@code isReceived} as {@code @NotNull}. The experience BFF is not expected
 * to know any of those, so the handler injects defaults before the request
 * leaves the domain tier. Without this the core rejects with HTTP 400 and
 * the saga aborts.
 */
@ExtendWith(MockitoExtension.class)
class RegisterApplicationDocumentHandlerTest {

    private static final UUID APPLICATION_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID GENERATED_DOCUMENT_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID PAYSLIP_TYPE_ID = UUID.fromString("00000000-0000-0000-0000-000000000d01");
    private static final UUID CALLER_TYPE_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @Mock
    private ApplicationDocumentApi applicationDocumentApi;

    @Mock
    private DocumentTypeApi documentTypeApi;

    private RegisterApplicationDocumentHandler handler;

    @BeforeEach
    void setUp() {
        handler = new RegisterApplicationDocumentHandler(applicationDocumentApi, documentTypeApi);
    }

    @Test
    void doHandle_resolvesDocumentTypeIdFromCode_andInjectsAllOtherDefaults() {
        var cmd = new RegisterApplicationDocumentCommand();
        cmd.withLoanApplicationId(APPLICATION_ID);
        cmd.setDocumentName("payslip.pdf");
        cmd.setMimeType("application/pdf");
        cmd.setFileSizeBytes(123L);
        cmd.setDocumentTypeCode("PAYSLIP");
        // documentTypeId, documentId, isMandatory, isReceived deliberately left null

        when(documentTypeApi.getDocumentTypeByCode(eq("PAYSLIP"), any()))
                .thenReturn(Mono.just(new DocumentType().documentTypeId(PAYSLIP_TYPE_ID).code("PAYSLIP")));
        when(applicationDocumentApi.createDocument(eq(APPLICATION_ID), any(), any()))
                .thenReturn(Mono.just(new ApplicationDocumentDTO(GENERATED_DOCUMENT_ID)));

        StepVerifier.create(handler.handle(cmd))
                .expectNext(GENERATED_DOCUMENT_ID)
                .verifyComplete();

        ArgumentCaptor<RegisterApplicationDocumentCommand> sent =
                ArgumentCaptor.forClass(RegisterApplicationDocumentCommand.class);
        verify(applicationDocumentApi).createDocument(eq(APPLICATION_ID), sent.capture(), any());
        var enriched = sent.getValue();
        assertThat(enriched.getDocumentTypeId()).isEqualTo(PAYSLIP_TYPE_ID);
        assertThat(enriched.getDocumentId()).isNotNull();
        assertThat(enriched.getIsMandatory()).isFalse();
        assertThat(enriched.getIsReceived()).isTrue();
    }

    @Test
    void doHandle_skipsLookup_whenCallerSuppliesDocumentTypeId() {
        var cmd = new RegisterApplicationDocumentCommand();
        cmd.withLoanApplicationId(APPLICATION_ID);
        cmd.setDocumentTypeId(CALLER_TYPE_ID);
        cmd.setDocumentTypeCode("PAYSLIP"); // present but ignored — id wins

        when(applicationDocumentApi.createDocument(eq(APPLICATION_ID), any(), any()))
                .thenReturn(Mono.just(new ApplicationDocumentDTO(GENERATED_DOCUMENT_ID)));

        StepVerifier.create(handler.handle(cmd))
                .expectNext(GENERATED_DOCUMENT_ID)
                .verifyComplete();

        verify(documentTypeApi, never()).getDocumentTypeByCode(any(), any());

        ArgumentCaptor<RegisterApplicationDocumentCommand> sent =
                ArgumentCaptor.forClass(RegisterApplicationDocumentCommand.class);
        verify(applicationDocumentApi).createDocument(eq(APPLICATION_ID), sent.capture(), any());
        assertThat(sent.getValue().getDocumentTypeId()).isEqualTo(CALLER_TYPE_ID);
    }

    @Test
    void doHandle_fallsBackToOther_whenNoCodeAndNoId() {
        var cmd = new RegisterApplicationDocumentCommand();
        cmd.withLoanApplicationId(APPLICATION_ID);
        // both documentTypeId and documentTypeCode left null

        when(applicationDocumentApi.createDocument(eq(APPLICATION_ID), any(), any()))
                .thenReturn(Mono.just(new ApplicationDocumentDTO(GENERATED_DOCUMENT_ID)));

        StepVerifier.create(handler.handle(cmd))
                .expectNext(GENERATED_DOCUMENT_ID)
                .verifyComplete();

        verify(documentTypeApi, never()).getDocumentTypeByCode(any(), any());

        ArgumentCaptor<RegisterApplicationDocumentCommand> sent =
                ArgumentCaptor.forClass(RegisterApplicationDocumentCommand.class);
        verify(applicationDocumentApi).createDocument(eq(APPLICATION_ID), sent.capture(), any());
        assertThat(sent.getValue().getDocumentTypeId()).isEqualTo(DEFAULT_DOCUMENT_TYPE_OTHER_ID);
    }

    @Test
    void doHandle_fallsBackToOther_whenCodeLookupFails() {
        var cmd = new RegisterApplicationDocumentCommand();
        cmd.withLoanApplicationId(APPLICATION_ID);
        cmd.setDocumentTypeCode("UNKNOWN_CODE");

        when(documentTypeApi.getDocumentTypeByCode(eq("UNKNOWN_CODE"), any()))
                .thenReturn(Mono.error(new RuntimeException("404 Not Found")));
        when(applicationDocumentApi.createDocument(eq(APPLICATION_ID), any(), any()))
                .thenReturn(Mono.just(new ApplicationDocumentDTO(GENERATED_DOCUMENT_ID)));

        StepVerifier.create(handler.handle(cmd))
                .expectNext(GENERATED_DOCUMENT_ID)
                .verifyComplete();

        ArgumentCaptor<RegisterApplicationDocumentCommand> sent =
                ArgumentCaptor.forClass(RegisterApplicationDocumentCommand.class);
        verify(applicationDocumentApi).createDocument(eq(APPLICATION_ID), sent.capture(), any());
        assertThat(sent.getValue().getDocumentTypeId()).isEqualTo(DEFAULT_DOCUMENT_TYPE_OTHER_ID);
    }

    @Test
    void doHandle_preservesCallerProvidedFlags_andDocumentId() {
        UUID callerDocId = UUID.randomUUID();
        var cmd = new RegisterApplicationDocumentCommand();
        cmd.withLoanApplicationId(APPLICATION_ID);
        cmd.setDocumentTypeId(CALLER_TYPE_ID);
        cmd.setDocumentId(callerDocId);
        cmd.setIsMandatory(true);
        cmd.setIsReceived(false);

        when(applicationDocumentApi.createDocument(eq(APPLICATION_ID), any(), any()))
                .thenReturn(Mono.just(new ApplicationDocumentDTO(GENERATED_DOCUMENT_ID)));

        StepVerifier.create(handler.handle(cmd))
                .expectNext(GENERATED_DOCUMENT_ID)
                .verifyComplete();

        ArgumentCaptor<RegisterApplicationDocumentCommand> sent =
                ArgumentCaptor.forClass(RegisterApplicationDocumentCommand.class);
        verify(applicationDocumentApi).createDocument(eq(APPLICATION_ID), sent.capture(), any());
        var enriched = sent.getValue();
        assertThat(enriched.getDocumentId()).isEqualTo(callerDocId);
        assertThat(enriched.getIsMandatory()).isTrue();
        assertThat(enriched.getIsReceived()).isFalse();
        assertThat(enriched.getDocumentTypeId()).isEqualTo(CALLER_TYPE_ID);
    }
}
