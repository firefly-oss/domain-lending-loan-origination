package com.firefly.domain.lending.loan.origination.core.loan.origination.commands;

import com.firefly.common.cqrs.command.Command;
import lombok.Data;

import java.util.UUID;

@Data
public class RejectApplicationCommand implements Command<UUID> {
}