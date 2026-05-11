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

import org.fireflyframework.cqrs.command.Command;
import com.firefly.core.lending.origination.sdk.model.ApplicationDocumentDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class RegisterApplicationDocumentCommand extends ApplicationDocumentDTO implements Command<UUID> {
    private UUID loanApplicationId;

    /**
     * Stable lookup code for document_type (e.g. PAYSLIP, ID_DOCUMENT, BANK_STATEMENT).
     * When the upstream caller supplies this code instead of the FK UUID, the
     * domain handler resolves it via the core DocumentTypeApi and falls back to
     * OTHER when the code is unknown. Mirrors the field of the same name on the
     * generated SDK request schema.
     */
    private String documentTypeCode;

    public RegisterApplicationDocumentCommand withLoanApplicationId(UUID loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
        return this;
    }
}
