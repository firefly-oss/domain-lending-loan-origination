package com.firefly.domain.lending.loan.origination.core.loan.origination.services;

import com.firefly.core.lending.origination.sdk.model.LoanApplicationDTO;
import com.firefly.domain.lending.loan.origination.core.loan.origination.commands.*;
import com.firefly.transactional.saga.core.SagaResult;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface LoanOriginationService {

    /**
     * Submits a loan application for processing. The application includes necessary
     * details such as product, amount, currency, and channel information.
     *
     * @param command the command containing the details of the loan application
     * @return a Mono emitting the result of the saga process for the submitted application
     */
    Mono<SagaResult> submitApplication(@Valid SubmitApplicationCommand command);
    
    /**
     * Attaches supporting documents to the specified loan application. These documents might include
     * income statements, bank statements, collateral documentation, and other necessary files.
     *
     * @param appId the unique identifier of the loan application to which documents are being attached
     * @param command the command containing the information and references for the documents to be attached
     * @return a reactive Mono emitting the result of the document attachment operation, encapsulated in a SagaResult
     */
    Mono<SagaResult> attachDocuments(UUID appId, @Valid RegisterApplicationDocumentCommand command);

    /**
     * Scores a loan application by persisting a model score along with metadata about the model
     * and its version.
     *
     * @param appId the unique identifier of the loan application to be scored
     * @param command the command containing the details necessary to score the application
     * @return a Mono emitting a SagaResult representing the outcome of the scoring operation
     */
    Mono<SagaResult> scoreApplication(UUID appId, @Valid RegisterUnderwritingScoreCommand command);
    

    /**
     * Updates the status of a loan application. This method processes the status change
     * of a loan application based on the details provided in the command and performs
     * necessary operations such as persisting the updated status and maintaining a history record.
     *
     * @param command the command containing details required to update the status of a loan application,
     *                including the loan application ID, new status information, and associated data.
     * @return a {@code Mono<SagaResult>} emitting the result of the status update process, encapsulating
     *         the outcome of the associated saga flow.
     */
    Mono<SagaResult> updateApplicationStatus(UpdateApplicationStatusCommand command);

    /**
     * Retrieves the details and state of a specified loan application.
     *
     * @param appId the unique identifier of the loan application to retrieve
     * @return a {@code Mono} emitting the loan application details, or empty if not found
     */
    Mono<LoanApplicationDTO> getApplication(UUID appId);
}
