package com.tieredlimit.application.service;

import com.tieredlimit.application.port.in.CalculateCommand;
import com.tieredlimit.application.port.in.CalculateResult;
import com.tieredlimit.application.port.in.SettlementCalculationUseCase;
import com.tieredlimit.domain.model.Apportionment;
import com.tieredlimit.domain.service.TierLimitService;

import java.util.List;

public class SettlementCalculationService implements SettlementCalculationUseCase {
    @Override
    public CalculateResult calculateTiers(CalculateCommand command) {
        TierLimitService tierLimitService = new TierLimitService();
        List<Apportionment> apportionments = tierLimitService.calculateTierLimitApportionment(command.settlement(), command.tierPolicy());

        command.settlement().replaceApportionments(apportionments);
        CalculateResult result = new CalculateResult(command.settlement());
        return result;
    }
}
