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


package io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import io.fireflysoftware.core.lending.origination.sdk.api.LoanApplicationStatusHistoryApi;
import io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.commands.RegisterLoanApplicationStatusHistoryCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class RegisterStatusHandler extends CommandHandler<RegisterLoanApplicationStatusHistoryCommand, UUID> {

    private final LoanApplicationStatusHistoryApi loanApplicationStatusHistoryApi;

    public RegisterStatusHandler(LoanApplicationStatusHistoryApi loanApplicationStatusHistoryApi) {
        this.loanApplicationStatusHistoryApi = loanApplicationStatusHistoryApi;
    }

    @Override
    protected Mono<UUID> doHandle(RegisterLoanApplicationStatusHistoryCommand cmd) {
        return loanApplicationStatusHistoryApi.createStatusHistory(cmd.getLoanApplicationId(), cmd, UUID.randomUUID().toString())
                .mapNotNull(statusHistoryDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(statusHistoryDTO)).getStatusHistoryId());
    }
}