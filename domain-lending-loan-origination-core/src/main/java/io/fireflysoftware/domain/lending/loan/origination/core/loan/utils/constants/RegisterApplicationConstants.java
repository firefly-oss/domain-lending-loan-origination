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


package io.fireflysoftware.domain.lending.loan.origination.core.loan.utils.constants;

public class RegisterApplicationConstants {

    // ============================== SAGA CONFIGURATION ==============================
    public static final String SAGA_REGISTER_APPLICATION = "RegisterApplicationSaga";
    public static final String SAGA_REGISTER_APPLICATION_DOCUMENT = "RegisterApplicationDocumentSaga";
    public static final String SAGA_REGISTER_SCORE = "RegisterScoreSaga";
    public static final String SAGA_UPDATE_APPLICATION_STATUS = "UpdateApplicationStatusSaga";

    // ============================== STEP IDENTIFIERS ==============================
    public static final String STEP_REGISTER_APPLICATION_PARTY = "registerApplicationParty";
    public static final String STEP_REGISTER_APPLICATION_DOCUMENT = "registerApplicationDocument";
    public static final String STEP_REGISTER_LOAN_APPLICATION = "registerLoanApplication";
    public static final String STEP_REGISTER_OFFER = "registerOffer";
    public static final String STEP_REGISTER_STATUS = "registerStatus";
    public static final String STEP_REGISTER_SCORE = "registerScore";
    public static final String STEP_REGISTER_DECISION = "registerDecision";
    public static final String STEP_RETRIEVE_APPLICATION_STATUS = "retrieveApplicationStatus";
    public static final String STEP_RETRIEVE_OLD_APPLICATION_STATUS = "retrieveOldApplicationStatus";
    public static final String STEP_RETRIEVE_APPLICATION = "retrieveApplication";
    public static final String STEP_UPDATE_APPLICATION_STATUS = "updateApplicationStatus";
    public static final String STEP_UPDATE_APPLICATION_STATUS_HISTORY = "updateApplicationStatusHistory";


    // ============================== COMPENSATE METHODS ==============================
    public static final String COMPENSATE_REMOVE_LOAN_APPLICATION = "removeLoanApplication";
    public static final String COMPENSATE_REMOVE_APPLICATION_PARTY = "removeApplicationParty";
    public static final String COMPENSATE_REMOVE_APPLICATION_DOCUMENT = "removeApplicationDocument";
    public static final String COMPENSATE_REMOVE_OFFER = "removeOffer";
    public static final String COMPENSATE_REMOVE_STATUS = "removeStatus";
    public static final String COMPENSATE_REMOVE_SCORE = "removeScore";
    public static final String COMPENSATE_REMOVE_DECISION = "removeDecision";

    // ============================== EVENT TYPES ==============================
    public static final String EVENT_LOAN_APPLICATION_REGISTERED = "loanApplication.registered";
    public static final String EVENT_APPLICATION_PARTY_REGISTERED = "applicationParty.registered";
    public static final String EVENT_APPLICATION_DOCUMENT_REGISTERED = "applicationDocument.registered";
    public static final String EVENT_OFFER_REGISTERED = "offer.registered";
    public static final String EVENT_STATUS_REGISTERED = "status.registered";
    public static final String EVENT_SCORE_REGISTERED = "score.registered";
    public static final String EVENT_DECISION_REGISTERED = "decision.registered";
    public static final String EVENT_APPLICATION_STATUS_RETRIEVED = "applicationStatus.retrieved";
    public static final String EVENT_OLD_APPLICATION_STATUS_RETRIEVED = "oldApplicationStatus.retrieved";
    public static final String EVENT_APPLICATION_RETRIEVED = "application.retrieved";
    public static final String EVENT_APPLICATION_STATUS_UPDATED = "applicationStatus.updated";
    public static final String EVENT_APPLICATION_STATUS_HISTORY_UPDATED = "applicationStatusHistory.updated";


}
