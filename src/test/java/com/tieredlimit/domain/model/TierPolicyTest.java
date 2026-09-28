package com.tieredlimit.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class TierPolicyTest {
    
    @Test
    @DisplayName("차수 정책이 정상 생성 되었습니다.")
    void 차수_정책이_정상_생성_되었습니다(){
        // given
        // db 데이터
        Ratio totalRatio = new Ratio(2000);
        Participant participant = new Participant("1234", "5678");
        String cardCompanyCode = "12";
        String cardType = "1";
        List<Tier> tierList = new ArrayList<>();

        int tierNo = 1;
        Money limit = new Money(200_000);

        Ratio cardRatio = new Ratio(500);
        Ratio participantRatio = new Ratio(500);
        Ratio companyRatio = new Ratio(1000);
        Tier tier = new Tier(tierNo, limit, new ShareRatio(cardRatio, participantRatio, companyRatio));

        tierList.add(tier);
        
        // when
        TierPolicy tierPolicy = new TierPolicy(totalRatio, participant, cardCompanyCode, cardType, tierList);

        // then
        assertAll(
                () -> assertThat(tierPolicy.getTotalRatio()).isEqualTo(totalRatio),
                () -> assertThat(tierPolicy.getParticipant()).isEqualTo(participant),
                () -> assertThat(tierPolicy.getCardCompanyCode()).isEqualTo(cardCompanyCode),
                () -> assertThat(tierPolicy.getCardType()).isEqualTo(cardType),
                () -> assertThat(tierPolicy.getTiers()).isEqualTo(tierList)
        );
    }

    @Test
    @DisplayName("차수한도는 이전 차수보다 커야한다.")
    void 차수한도는_이전_차수보다_커야한다() {
        // given
        // db 데이터
        Ratio totalRatio = new Ratio(2000);
        Participant participant = new Participant("1234", "5678");
        String cardCompanyCode = "12";
        String cardType = "1";
        List<Tier> tierList = new ArrayList<>();

        Ratio cardRatio = new Ratio(500);
        Ratio participantRatio = new Ratio(500);
        Ratio companyRatio = new Ratio(1000);

        Tier tier1 = new Tier(2, new Money(200_000), new ShareRatio(cardRatio, participantRatio, companyRatio));
        Tier tier2 = new Tier(1, new Money(300_000), new ShareRatio(cardRatio, participantRatio, companyRatio));

        tierList.add(tier1);
        tierList.add(tier2);

        assertThrows(IllegalArgumentException.class,
                () -> new TierPolicy(totalRatio, participant, cardCompanyCode, cardType, tierList)
        );
    }

    @Test
    @DisplayName("차수마다 총 할인율은 동일해야한다.")
    void 차수마다_총_할인율은_동일해야한다() {
        // given
        // db 데이터
        Ratio totalRatio = new Ratio(2000);
        Participant participant = new Participant("1234", "5678");
        String cardCompanyCode = "12";
        String cardType = "1";
        List<Tier> tiers = new ArrayList<>();

        Ratio cardRatio = new Ratio(500);
        Ratio participantRatio = new Ratio(500);
        Ratio companyRatio = new Ratio(1000);

        Tier tier1 = new Tier(1, new Money(200_000), new ShareRatio(cardRatio, participantRatio, companyRatio));

        Ratio secondCardRatio = new Ratio(1000);
        Ratio secondParticipantRatio = new Ratio(1000);
        Ratio secondCompanyRatio = new Ratio(3000);

        Tier tier2 = new Tier(2, new Money(300_000), new ShareRatio(secondCardRatio, secondParticipantRatio, secondCompanyRatio));

        tiers.add(tier1);
        tiers.add(tier2);

        //when
        //then
        assertThatThrownBy(() -> new TierPolicy(totalRatio, participant, cardCompanyCode, cardType, tiers))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(String.format("차수마다 총 할인율은 동일해야한다. 정책 총 할인율: %d, %d차 총할인율: %d", 2000, 2, 5000));

    }

    @Test
    @DisplayName("정책 할인율과 각 차수 총 할인율은 동일해야한다.")
    void 정책_할인율과_각_차수_총_할인율은_동일해야한다() {
        // given
        // db 데이터
        Ratio totalRatio = new Ratio(2000);
        Participant participant = new Participant("1234", "5678");
        String cardCompanyCode = "12";
        String cardType = "1";
        List<Tier> tiers = new ArrayList<>();

        Ratio cardRatio = new Ratio(1000);
        Ratio participantRatio = new Ratio(1000);
        Ratio companyRatio = new Ratio(3000);

        Tier tier1 = new Tier(1, new Money(200_000), new ShareRatio(cardRatio, participantRatio, companyRatio));

        Ratio secondCardRatio = new Ratio(1000);
        Ratio secondParticipantRatio = new Ratio(1000);
        Ratio secondCompanyRatio = new Ratio(3000);

        Tier tier2 = new Tier(2, new Money(300_000), new ShareRatio(secondCardRatio, secondParticipantRatio, secondCompanyRatio));

        tiers.add(tier1);
        tiers.add(tier2);

        //when
        //then
        assertThatThrownBy(() -> new TierPolicy(totalRatio, participant, cardCompanyCode, cardType, tiers))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(String.format("차수마다 총 할인율은 동일해야한다. 정책 총 할인율: %d, %d차 총할인율: %d", 2000, 1, 5000));

    }

}