package com.tieredlimit.application;

import com.tieredlimit.application.port.in.CalculateCommand;
import com.tieredlimit.application.port.in.CalculateResult;
import com.tieredlimit.application.port.in.SettlementCalculationUseCase;
import com.tieredlimit.application.service.SettlementCalculationService;
import com.tieredlimit.domain.model.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SettlementCalculationServiceTest {

    @Test
    void calculateTiers_브랜드A_3차_누진분담(){
        //given
        Transaction transaction = new Transaction(new Money(500_000), new Money(250_000), new Money(250_000));
        List<Apportionment> apportionments = new ArrayList<>();

        Settlement settlement = new Settlement(transaction, apportionments);
        Tier tier1 = new Tier(1, new Money(300_000),new ShareRatio(new Ratio(1500), new Ratio(2000), new Ratio(1500)));
        Tier tier2 = new Tier(2, new Money(400_000),new ShareRatio(new Ratio(0), new Ratio(2500), new Ratio(2500)));
        Tier tier3 = new Tier(3, new Money(600_000),new ShareRatio(new Ratio(0), new Ratio(2000), new Ratio(3000)));

        List<Tier> tiers = new ArrayList<>();
        tiers.add(tier1);
        tiers.add(tier2);
        tiers.add(tier3);

        TierPolicy tierPolicy = new TierPolicy(new Ratio(5000), new Participant("1234", "5678"), "10", "2", tiers);

        CalculateCommand calculateCommand = new CalculateCommand(settlement, tierPolicy);

        //when
        SettlementCalculationUseCase useCase = new SettlementCalculationService();
        CalculateResult calculateResult = useCase.calculateTiers(calculateCommand);

        //then
        assertAll(
                () -> assertThat(calculateResult.settlement().getApportionments().get(0).amount()).isEqualTo(new Money(45_000)),
                () -> assertThat(calculateResult.settlement().getApportionments().get(1).amount()).isEqualTo(new Money(105_000)),
                () -> assertThat(calculateResult.settlement().getApportionments().get(2).amount()).isEqualTo(new Money(100_000))
        );
    }
}
