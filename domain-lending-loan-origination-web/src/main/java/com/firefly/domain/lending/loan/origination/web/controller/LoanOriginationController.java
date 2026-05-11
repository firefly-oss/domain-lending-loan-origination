package com.firefly.domain.lending.loan.origination.web.controller;

import com.firefly.core.lending.origination.sdk.model.ApplicationPartyDTO;
import com.firefly.core.lending.origination.sdk.model.LoanApplicationDTO;
import com.firefly.core.lending.origination.sdk.model.SimulationDTO;
import com.firefly.domain.lending.loan.origination.core.applicationparty.commands.UpdateApplicationEmploymentDataCommand;
import com.firefly.domain.lending.loan.origination.core.loan.origination.commands.*;
import com.firefly.domain.lending.loan.origination.core.loan.origination.queries.GetApplicationStatusQuery;
import com.firefly.domain.lending.loan.origination.core.loan.origination.queries.GetLoanApplicationQuery;
import com.firefly.domain.lending.loan.origination.core.loan.origination.services.LoanOriginationService;
import com.firefly.domain.lending.loan.origination.core.simulation.commands.PersistSimulationCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

import static com.firefly.domain.lending.loan.origination.core.loan.utils.constants.RegisterApplicationConstants.STEP_REGISTER_APPLICATION_DOCUMENT;
import static com.firefly.domain.lending.loan.origination.core.loan.utils.constants.RegisterApplicationConstants.STEP_REGISTER_LOAN_APPLICATION;

@RestController
@RequestMapping("/api/v1/loan-applications")
@RequiredArgsConstructor
@Tag(name = "Loan Origination", description = "CQ queries and registration for Loan Origination")
public class LoanOriginationController {

    private final LoanOriginationService loanOriginationService;

    @Operation(summary = "Submit application", description = "Submit an application with product, amount, currency, and channel.")
    @PostMapping
    public Mono<ResponseEntity<Map<String, UUID>>> submitApplication(@Valid @RequestBody SubmitApplicationCommand command) {
        return loanOriginationService.submitApplication(command)
                .map(result -> {
                    if (!result.isSuccess()) {
                        throw new IllegalStateException(
                                "RegisterApplicationSaga failed at step: "
                                        + result.firstErrorStepId().orElse("unknown"));
                    }
                    UUID id = result.resultOf(STEP_REGISTER_LOAN_APPLICATION, UUID.class)
                            .orElseThrow(() -> new IllegalStateException(
                                    "RegisterApplicationSaga completed without a loanApplicationId result"));
                    return ResponseEntity.ok(Map.of("loanApplicationId", id));
                });
    }

    @Operation(summary = "Attach documents", description = "Attach supporting documents including income, statements, and collateral.")
    @PostMapping("/{appId}/documents")
    public Mono<ResponseEntity<Map<String, UUID>>> attachDocuments(@PathVariable UUID appId, @Valid @RequestBody RegisterApplicationDocumentCommand command) {
        return loanOriginationService.attachDocuments(appId, command)
                .map(result -> {
                    if (!result.isSuccess()) {
                        throw new IllegalStateException(
                                "RegisterApplicationDocumentSaga failed at step: "
                                        + result.firstErrorStepId().orElse("unknown"));
                    }
                    UUID id = result.resultOf(STEP_REGISTER_APPLICATION_DOCUMENT, UUID.class)
                            .orElseThrow(() -> new IllegalStateException(
                                    "RegisterApplicationDocumentSaga completed without an applicationDocumentId result"));
                    return ResponseEntity.ok(Map.of("applicationDocumentId", id));
                });
    }

    @Operation(summary = "Withdraw application", description = "Withdraw the application by applicant request.")
    @PostMapping("/{appId}/withdraw")
    public Mono<ResponseEntity<Object>> withdrawApplication(@PathVariable UUID appId) {
        return loanOriginationService
                .updateApplicationStatus(UpdateApplicationStatusCommand.builder()
                        .loanApplicationId(appId)
                        .applicationQuery(GetLoanApplicationQuery
                                .builder().loanApplicationId(appId)
                                .build())
                        .applicationStatusQuery(GetApplicationStatusQuery
                                .builder().applicationStatusCode("CANCELLED")
                                .build())
                        .statusHistoryCommand(RegisterLoanApplicationStatusHistoryCommand.builder().build())
                        .build())
                .thenReturn(ResponseEntity.ok().build());
    }

    @Operation(summary = "Score application", description = "Persist model score with model and version metadata.")
    @PostMapping("/{appId}/score")
    public Mono<ResponseEntity<Object>> scoreApplication(@PathVariable UUID appId, @Valid @RequestBody RegisterUnderwritingScoreCommand command) {
        return loanOriginationService.scoreApplication(appId, command)
                .thenReturn(ResponseEntity.ok().build());
    }

    @Operation(summary = "Approve application", description = "Approve with terms including rate, tenor, and fees aligned with product rules.")
    @PostMapping("/{appId}/approve")
    public Mono<ResponseEntity<Object>> approveApplication(@PathVariable UUID appId) {
        return loanOriginationService
                .updateApplicationStatus(UpdateApplicationStatusCommand.builder()
                        .loanApplicationId(appId)
                        .applicationQuery(GetLoanApplicationQuery
                                .builder().loanApplicationId(appId)
                                .build())
                        .applicationStatusQuery(GetApplicationStatusQuery
                                .builder().applicationStatusCode("APPROVED")
                                .build())
                        .statusHistoryCommand(RegisterLoanApplicationStatusHistoryCommand.builder().build())
                        .build())
                .thenReturn(ResponseEntity.ok().build());
    }

    @Operation(summary = "Reject application", description = "Reject with reason for risk, eligibility, or documentation issues.")
    @PostMapping("/{appId}/reject")
    public Mono<ResponseEntity<Object>> rejectApplication(@PathVariable UUID appId) {
        return loanOriginationService
                .updateApplicationStatus(UpdateApplicationStatusCommand.builder()
                        .loanApplicationId(appId)
                        .applicationQuery(GetLoanApplicationQuery
                                .builder().loanApplicationId(appId)
                                .build())
                        .applicationStatusQuery(GetApplicationStatusQuery
                                .builder().applicationStatusCode("REJECTED")
                                .build())
                        .statusHistoryCommand(RegisterLoanApplicationStatusHistoryCommand.builder().build())
                        .build())
                .thenReturn(ResponseEntity.ok().build());
    }

    @Operation(summary = "Get application", description = "Retrieve application state and audit log.")
    @GetMapping("/{appId}")
    public Mono<ResponseEntity<LoanApplicationDTO>> getApplication(@PathVariable UUID appId) {
        return loanOriginationService.getApplication(appId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @Operation(
            operationId = "persistLendingSimulation",
            summary = "Persist a pre-computed simulation",
            description = "Persists a pre-computed loan simulation through the core lending loan-origination service."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Simulation persisted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid simulation payload")
    })
    @PostMapping("/simulations")
    public Mono<ResponseEntity<SimulationDTO>> persistLendingSimulation(
            @Valid @RequestBody PersistSimulationCommand command) {
        return loanOriginationService.persistSimulation(command)
                .map(simulation -> ResponseEntity.status(HttpStatus.CREATED).body(simulation));
    }

    @Operation(
            operationId = "updateApplicationEmploymentData",
            summary = "Update primary applicant employment data",
            description = "Updates the economic / employment data of the primary application party associated with the loan application."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employment data updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid employment data payload"),
            @ApiResponse(responseCode = "404", description = "Application or primary application party not found")
    })
    @PatchMapping("/{applicationId}/employment-data")
    public Mono<ResponseEntity<ApplicationPartyDTO>> updateApplicationEmploymentData(
            @PathVariable UUID applicationId,
            @Valid @RequestBody UpdateApplicationEmploymentDataCommand command) {
        command.setApplicationId(applicationId);
        return loanOriginationService.updateApplicationEmploymentData(command)
                .map(ResponseEntity::ok);
    }
}
