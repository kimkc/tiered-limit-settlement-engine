package com.tieredlimit.adapter.in.web;

import com.tieredlimit.application.port.in.CalculateCommand;
import com.tieredlimit.application.port.in.CalculateResult;
import com.tieredlimit.application.port.in.SettlementCalculationUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SettlementCalculationController {

    private final SettlementCalculationUseCase settlementCalculationUseCase;

    SettlementCalculationController(SettlementCalculationUseCase settlementCalculationUseCase) {
        this.settlementCalculationUseCase = settlementCalculationUseCase;
    }

    @PostMapping("/settlements/calculate")
    public CalculateResponse calculate(@RequestBody CalculateRequest request){
        CalculateResult calculateResult = settlementCalculationUseCase.calculateTiers(request.toCommand());
        return CalculateResponse.from(calculateResult.settlement());
    }

}
