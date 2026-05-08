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

package com.firefly.domain.lending.loan.origination.core.loan.utils.constants;

import java.util.UUID;

/**
 * Canonical UUIDs for the three FK lookup columns enforced as
 * {@code @NotNull} by {@code core-lending-loan-origination}'s
 * {@code LoanApplicationDTO}:
 *
 * <ul>
 *   <li>{@code applicationStatusId}</li>
 *   <li>{@code applicationSubStatusId}</li>
 *   <li>{@code submissionChannelId}</li>
 * </ul>
 *
 * The corresponding rows are seeded by Flyway migration
 * {@code V16__Seed_Default_Application_Lookups.sql} in the core service.
 * The domain saga uses these as fall-back values when the experience tier
 * does not supply explicit IDs in the {@link RegisterLoanApplicationCommand}.
 *
 * <p>Future enhancement: replace these compile-time constants with a live
 * {@code ApplicationStatusApi.getApplicationStatusByCode(...)} (and equivalent
 * SDK calls for sub-status and submission channel once those controllers are
 * exposed). At that point this class can be removed in favour of a small
 * resolver service.
 */
public final class ApplicationLookupDefaults {

    /** Lookup code for the default application status. */
    public static final String DEFAULT_APPLICATION_STATUS_CODE = "DRAFT";

    /** Lookup code for the default application sub-status. */
    public static final String DEFAULT_APPLICATION_SUB_STATUS_CODE = "PENDING_DOCUMENTS";

    /** Lookup code for the default submission channel. */
    public static final String DEFAULT_SUBMISSION_CHANNEL_CODE = "ONLINE";

    /** UUID of the seeded {@code DRAFT} row in {@code application_status}. */
    public static final UUID DEFAULT_APPLICATION_STATUS_ID =
            UUID.fromString("00000000-0000-0000-0000-000000000a01");

    /** UUID of the seeded {@code PENDING_DOCUMENTS} row in {@code application_sub_status}. */
    public static final UUID DEFAULT_APPLICATION_SUB_STATUS_ID =
            UUID.fromString("00000000-0000-0000-0000-000000000b01");

    /** UUID of the seeded {@code ONLINE} row in {@code submission_channel}. */
    public static final UUID DEFAULT_SUBMISSION_CHANNEL_ID =
            UUID.fromString("00000000-0000-0000-0000-000000000c01");

    private ApplicationLookupDefaults() {
        // utility class — no instances
    }
}
