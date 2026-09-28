package com.tieredlimit.config;

import com.tieredlimit.application.port.in.SettlementCalculationUseCase;
import com.tieredlimit.application.service.SettlementCalculationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig{

    @Bean
    public SettlementCalculationUseCase settlementCalculationUseCase(){
        return new SettlementCalculationService();
    }
}
