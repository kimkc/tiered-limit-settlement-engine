package com.tieredlimit.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ShareRatioTest {

    @Test
    @DisplayName("분담율이 NULL일 수 없습니다.")
    void 분담율이_NULL일_수_없습니다() {
        Ratio card = null;
        Ratio participant = new Ratio(1500);
        Ratio company = new Ratio(1000);

        assertThrows(IllegalArgumentException.class, () ->
                new ShareRatio(card, participant, company)
        );
    }

    @ParameterizedTest(name = "[{index}] card={0}bp, participant={1}bp, company={2}bp이면 총합은 {3}bp이다")
    @MethodSource("provideValidRatios")
    @DisplayName("허용된 분담률 합계이면 ShareRatio가 정상 생성된다")
    void 분담률이_허용된_합계이면_정상_생성된다(Ratio card, Ratio participant, Ratio company, long expectedTotal) {
        ShareRatio shareRatio = new ShareRatio(card, participant, company);

        assertEquals(expectedTotal, shareRatio.totalRatio());
    }

    private static Stream<Arguments> provideValidRatios() {
        return Stream.of(
                // 5000bp 케이스
                Arguments.of(new Ratio(500), new Ratio(1500), new Ratio(3000), 5000L),
                // 2000bp 케이스
                Arguments.of(new Ratio(500), new Ratio(500), new Ratio(1000), 2000L)
        );
    }

}
