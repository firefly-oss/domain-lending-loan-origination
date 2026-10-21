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

import io.fireflysoftware.core.lending.origination.sdk.api.UnderwritingDecisionApi;
import io.fireflysoftware.core.lending.origination.sdk.model.PaginationResponseUnderwritingDecisionDTO;
import io.fireflysoftware.domain.lending.loan.origination.core.loan.origination.queries.GetApplicationDecisionsQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetApplicationDecisionsHandlerTest {

    @Mock
    private UnderwritingDecisionApi underwritingDecisionApi;

    @InjectMocks
    private GetApplicationDecisionsHandler handler;

    @Test
    void doHandle_shouldReturnDecisions() {
        UUID applicationId = UUID.randomUUID();
        PaginationResponseUnderwritingDecisionDTO expected = new PaginationResponseUnderwritingDecisionDTO();

        when(underwritingDecisionApi.findAllDecisions(applicationId, null, null, null, null, null))
                .thenReturn(Mono.just(expected));

        GetApplicationDecisionsQuery query = GetApplicationDecisionsQuery.builder()
                .applicationId(applicationId)
                .build();

        StepVerifier.create(handler.doHandle(query))
                .expectNext(expected)
                .verifyComplete();

        verify(underwritingDecisionApi).findAllDecisions(applicationId, null, null, null, null, null);
    }
}
