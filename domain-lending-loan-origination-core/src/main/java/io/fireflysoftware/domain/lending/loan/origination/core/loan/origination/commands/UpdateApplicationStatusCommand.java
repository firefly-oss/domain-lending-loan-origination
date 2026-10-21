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


package io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.commands;

import org.fireflyframework.cqrs.command.Command;
import io.fireflysoftware.core.lending.origination.sdk.model.LoanApplicationDTO;
import io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.queries.GetApplicationStatusQuery;
import io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.queries.GetLoanApplicationQuery;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UpdateApplicationStatusCommand implements Command<UUID> {
    private UUID loanApplicationId;
    private UUID applicationStatusId;
    private GetLoanApplicationQuery applicationQuery;
    private GetApplicationStatusQuery applicationStatusQuery;
    private LoanApplicationDTO loanApplication;
    private RegisterLoanApplicationStatusHistoryCommand statusHistoryCommand;

    public UpdateApplicationStatusCommand withApplicationStatusId(UUID applicationStatusId) {
        this.applicationStatusId = applicationStatusId;
        return this;
    }

    public UpdateApplicationStatusCommand withLoanApplication(LoanApplicationDTO loanApplication) {
        this.loanApplication = loanApplication;
        return this;
    }
}