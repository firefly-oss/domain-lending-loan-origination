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

import io.fireflysoftware.core.lending.origination.sdk.api.ApplicationVerificationApi;
import io.fireflysoftware.core.lending.origination.sdk.model.PaginationResponseApplicationVerificationDTO;
import io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.queries.GetApplicationVerificationsQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@QueryHandlerComponent
public class GetApplicationVerificationsHandler extends QueryHandler<GetApplicationVerificationsQuery, PaginationResponseApplicationVerificationDTO> {

    private final ApplicationVerificationApi applicationVerificationApi;

    @Override
    protected Mono<PaginationResponseApplicationVerificationDTO> doHandle(GetApplicationVerificationsQuery query) {
        return applicationVerificationApi.findAllVerifications(query.getApplicationId(), null, null, null, null, null);
    }
}
