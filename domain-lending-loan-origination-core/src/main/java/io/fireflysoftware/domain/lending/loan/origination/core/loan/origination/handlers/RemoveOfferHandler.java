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
import io.fireflysoftware.core.lending.origination.sdk.api.ProposedOfferApi;
import io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.commands.RemoveProposedOfferCommand;
import reactor.core.publisher.Mono;
import java.util.UUID;

@CommandHandlerComponent
public class RemoveOfferHandler extends CommandHandler<RemoveProposedOfferCommand, Void> {

    private final ProposedOfferApi proposedOfferApi;

    public RemoveOfferHandler(ProposedOfferApi proposedOfferApi) {
        this.proposedOfferApi = proposedOfferApi;
    }

    @Override
    protected Mono<Void> doHandle(RemoveProposedOfferCommand cmd) {
        return proposedOfferApi.deleteOffer(cmd.loanApplicationId(), cmd.proposedOfferId(), UUID.randomUUID().toString()).then();
    }
}