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
 * Canonical UUIDs for the {@code document_type} lookup table seeded by the core
 * Flyway migration {@code V17__Seed_Default_Document_Types.sql}. The domain
 * handler uses {@link #DEFAULT_DOCUMENT_TYPE_OTHER_ID} as a safe fall-back when
 * the experience BFF does not supply a {@code documentTypeId} or supplies a
 * {@code documentTypeCode} the core does not recognise.
 *
 * <p>Sister class to {@link ApplicationLookupDefaults}. Kept separate to avoid
 * conflating the loan-application FK defaults (status / sub-status / channel)
 * with the per-document type defaults.
 */
public final class DocumentTypeDefaults {

    /** Lookup code for the catch-all {@code OTHER} document type. */
    public static final String DEFAULT_DOCUMENT_TYPE_OTHER_CODE = "OTHER";

    /** UUID of the seeded {@code OTHER} row in {@code document_type}. */
    public static final UUID DEFAULT_DOCUMENT_TYPE_OTHER_ID =
            UUID.fromString("00000000-0000-0000-0000-000000000d99");

    private DocumentTypeDefaults() {
        // utility class — no instances
    }
}
