package com.tieredlimit.application.port.in;

public interface SettlementCalculationUseCase {
    CalculateResult calculateTiers(CalculateCommand command);
}
