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
import io.fireflysoftware.core.lending.origination.sdk.api.ApplicationDocumentApi;
import io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.commands.RemoveApplicationDocumentCommand;
import reactor.core.publisher.Mono;
import java.util.UUID;

@CommandHandlerComponent
public class RemoveApplicationDocumentHandler extends CommandHandler<RemoveApplicationDocumentCommand, Void> {

    private final ApplicationDocumentApi applicationDocumentApi;

    public RemoveApplicationDocumentHandler(ApplicationDocumentApi applicationDocumentApi) {
        this.applicationDocumentApi = applicationDocumentApi;
    }

    @Override
    protected Mono<Void> doHandle(RemoveApplicationDocumentCommand cmd) {
        return applicationDocumentApi.deleteDocument(cmd.loanApplicationId(), cmd.applicationDocumentId(), UUID.randomUUID().toString()).then();
    }
}
