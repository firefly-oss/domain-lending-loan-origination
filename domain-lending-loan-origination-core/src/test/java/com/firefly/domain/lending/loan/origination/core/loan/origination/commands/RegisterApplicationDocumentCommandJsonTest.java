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

package com.firefly.domain.lending.loan.origination.core.loan.origination.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the Jackson contract for {@link RegisterApplicationDocumentCommand}:
 * {@code documentTypeCode} must be readable from the inbound JSON body (so the
 * BFF can supply it) but must NEVER appear in serialized output, because the
 * domain handler forwards the same command instance to core via the SDK and
 * core's {@code ApplicationDocumentDTO} has no such field — its idempotency
 * filter rejects any unknown JSON property with HTTP 400.
 */
class RegisterApplicationDocumentCommandJsonTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void documentTypeCode_isReadFromIncomingJson() throws Exception {
        String inbound = "{\"documentTypeCode\":\"PAYSLIP\",\"documentName\":\"x.pdf\"}";

        RegisterApplicationDocumentCommand cmd =
                mapper.readValue(inbound, RegisterApplicationDocumentCommand.class);

        assertThat(cmd.getDocumentTypeCode()).isEqualTo("PAYSLIP");
        assertThat(cmd.getDocumentName()).isEqualTo("x.pdf");
    }

    @Test
    void documentTypeCode_isNeverSerializedToOutgoingJson() throws Exception {
        RegisterApplicationDocumentCommand cmd = new RegisterApplicationDocumentCommand();
        cmd.setDocumentTypeCode("PAYSLIP");
        cmd.setDocumentName("x.pdf");

        String outbound = mapper.writeValueAsString(cmd);

        assertThat(outbound)
                .as("documentTypeCode is a domain-only convenience and must not leak to core")
                .doesNotContain("documentTypeCode")
                .doesNotContain("PAYSLIP");
        assertThat(outbound).contains("\"documentName\":\"x.pdf\"");
    }
}
