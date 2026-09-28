package com.tieredlimit.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

public class TierTest {

    @Test
    @DisplayName("차수의 정상 값이 생성 되었습니다.")
    void 차수의_정상_값이_생성_되었습니다(){
        // given
        int tierNo = 1;
        Money limit = new Money(200_000);

        Ratio card = new Ratio(500);
        Ratio participant = new Ratio(500);
        Ratio company = new Ratio(1000);

        // when
        Tier tier = new Tier(tierNo, limit, new ShareRatio(card, participant, company));

        // then
        assertAll(
                () -> assertThat(tier.tierNo()).isEqualTo(tierNo),
                () -> assertThat(tier.limitAmount()).isEqualTo(limit),
                () -> assertThat(tier.shareRatio()).isEqualTo(new ShareRatio(card, participant, company))
        );
    }

    @Test
    @DisplayName("분담률은 NULL 일 수 없습니다.")
    void 분담률은_NULL_일_수_없습니다() {
        assertThrows(IllegalArgumentException.class, () ->
                new Tier(1, new Money(200000), null)
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100})
    @DisplayName("차수가 양수여야 한다.")
    void 차수가_양수여야_한다(int invalidTier) {
        // given
        Ratio card = new Ratio(500);
        Ratio participant = new Ratio(500);
        Ratio company = new Ratio(1000);
        Money limitAmount = new Money(200000);
        ShareRatio shareRatio = new ShareRatio(card, participant, company);

        // when & then
        assertThatThrownBy(() -> new Tier(invalidTier, limitAmount, shareRatio))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("차수는 0이거나 음수일 수 없습니다.");
    }

    @Test
    @DisplayName("한도금액에 null이 포함될 수 없습니다.")
    void 한도금액에_null이_포함될_수_없습니다() {
        Ratio card = new Ratio(500);
        Ratio participant = new Ratio(500);
        Ratio company = new Ratio(1000);

        assertThrows(IllegalArgumentException.class, () ->
                new Tier(1, null, new ShareRatio(card, participant, company))
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100})
    @DisplayName("한도금액이 0이나 음수 일 수 없습니다.")
    void 한도금액이_0이나_음수_일_수_없습니다(int invalidAmount) {
        Ratio card = new Ratio(500);
        Ratio participant = new Ratio(500);
        Ratio company = new Ratio(1000);
        ShareRatio shareRatio = new ShareRatio(card, participant, company);

        assertThatThrownBy(() ->
                new Tier(1, new Money(invalidAmount), shareRatio))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("한도금액은 0이거나 음수일 수 없습니다.");
    }

}
