package com.firefly.domain.lending.loan.origination.core.loan.origination.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import com.firefly.core.lending.origination.sdk.api.ApplicationDocumentApi;
import com.firefly.core.lending.origination.sdk.api.DocumentTypeApi;
import com.firefly.domain.lending.loan.origination.core.loan.origination.commands.RegisterApplicationDocumentCommand;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

import static com.firefly.domain.lending.loan.origination.core.loan.utils.constants.DocumentTypeDefaults.DEFAULT_DOCUMENT_TYPE_OTHER_ID;

@Slf4j
@CommandHandlerComponent
public class RegisterApplicationDocumentHandler extends CommandHandler<RegisterApplicationDocumentCommand, UUID> {

    private final ApplicationDocumentApi applicationDocumentApi;
    private final DocumentTypeApi documentTypeApi;

    public RegisterApplicationDocumentHandler(ApplicationDocumentApi applicationDocumentApi,
                                              DocumentTypeApi documentTypeApi) {
        this.applicationDocumentApi = applicationDocumentApi;
        this.documentTypeApi = documentTypeApi;
    }

    @Override
    protected Mono<UUID> doHandle(RegisterApplicationDocumentCommand cmd) {
        return resolveDocumentTypeId(cmd)
                .flatMap(documentTypeId -> {
                    enrichDefaults(cmd, documentTypeId);
                    return applicationDocumentApi.createDocument(
                                    cmd.getLoanApplicationId(), cmd, UUID.randomUUID().toString())
                            .mapNotNull(dto -> Objects.requireNonNull(
                                    Objects.requireNonNull(dto).getApplicationDocumentId()));
                });
    }

    /**
     * Returns a non-null documentTypeId, in this priority order:
     *   (1) cmd.documentTypeId if already supplied by the caller,
     *   (2) the UUID looked up via core's DocumentTypeApi when documentTypeCode is set,
     *   (3) DEFAULT_DOCUMENT_TYPE_OTHER_ID as fallback.
     * The lookup never propagates an error: an unknown code or a transient core-side
     * failure both degrade to OTHER, since this lookup is metadata, not load-bearing.
     */
    private Mono<UUID> resolveDocumentTypeId(RegisterApplicationDocumentCommand cmd) {
        if (cmd.getDocumentTypeId() != null) {
            return Mono.just(cmd.getDocumentTypeId());
        }
        String code = cmd.getDocumentTypeCode();
        if (code == null || code.isBlank()) {
            return Mono.just(DEFAULT_DOCUMENT_TYPE_OTHER_ID);
        }
        return documentTypeApi.getDocumentTypeByCode(code, null)
                .map(documentType -> {
                    UUID resolved = documentType.getDocumentTypeId();
                    return resolved != null ? resolved : DEFAULT_DOCUMENT_TYPE_OTHER_ID;
                })
                .onErrorResume(error -> {
                    log.warn("Could not resolve documentTypeCode='{}' via core; falling back to OTHER. cause={}",
                            code, error.toString());
                    return Mono.just(DEFAULT_DOCUMENT_TYPE_OTHER_ID);
                })
                .defaultIfEmpty(DEFAULT_DOCUMENT_TYPE_OTHER_ID);
    }

    /**
     * Fills in fields the experience BFF is not expected to know, so the core
     * controller's @Valid does not reject the request. loanApplicationId is
     * already set upstream by LoanOriginationServiceImpl.attachDocuments via
     * command.withLoanApplicationId(appId).
     *
     *   - documentId    : freshly minted UUID. The column {@code application_document.document_id}
     *                     is currently an orphan UUID column (no FK, no business consumer
     *                     anywhere in the codebase, see V2/V4 Flyway migrations). The
     *                     core DTO marks it as {@code @NotNull}, so we have to send
     *                     something; a fresh UUID is the least surprising filler. If
     *                     the column is ever wired to a real Document entity, this
     *                     handler must change accordingly.
     *   - documentTypeId: resolved value, see {@link #resolveDocumentTypeId}.
     *   - isMandatory   : false (ad-hoc upload, not a required-checklist entry).
     *   - isReceived    : true  (we are receiving the file right now).
     */
    private void enrichDefaults(RegisterApplicationDocumentCommand cmd, UUID documentTypeId) {
        if (cmd.getDocumentId() == null) {
            cmd.setDocumentId(UUID.randomUUID());
        }
        cmd.setDocumentTypeId(documentTypeId);
        if (cmd.getIsMandatory() == null) {
            cmd.setIsMandatory(false);
        }
        if (cmd.getIsReceived() == null) {
            cmd.setIsReceived(true);
        }
    }
}
