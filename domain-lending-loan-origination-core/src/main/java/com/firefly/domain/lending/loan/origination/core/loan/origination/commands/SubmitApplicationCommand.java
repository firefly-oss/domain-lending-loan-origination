package com.firefly.domain.lending.loan.origination.core.loan.origination.commands;

import com.firefly.common.cqrs.command.Command;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class SubmitApplicationCommand implements Command<UUID> {

    private RegisterLoanApplicationCommand application;
    private List<RegisterApplicationPartyCommand> parties;
    private List<RegisterApplicationDocumentCommand> documents;
    private List<RegisterApplicationCollateralCommand> collaterals;
    private List<RegisterProposedOfferCommand> offers;
    private List<RegisterLoanApplicationStatusHistoryCommand> statusHistories;
    private RegisterUnderwritingScoreCommand score;
    private RegisterUnderwritingDecisionCommand decision;
}