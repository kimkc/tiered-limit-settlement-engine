package com.tieredlimit.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RatioTest {

    @Test
    @DisplayName("분담율은 음수일 수 없다")
    void 음수면_예외가_발생한다() {
        assertThrows(IllegalArgumentException.class, () -> new Ratio(-1));
    }

    @Test
    @DisplayName("10000 초과할 수 없다")
    void 정수_10000을_초과할_수_없다() {
        assertThrows(IllegalArgumentException.class, () -> new Ratio(10001));
    }

    @Test
    @DisplayName("0은 허용된다")
    void 경계값_0은_허용된다() {
        assertDoesNotThrow(() -> new Ratio(0));
    }

    @Test
    @DisplayName("10000은 허용된다")
    void 경계값_10000은_허용된다() {
        assertDoesNotThrow(() -> new Ratio(10000));
    }

    @Test
    @DisplayName("정상 값이 보존된다.")
    void 정상값이_보존_된다() {
        long value = 50;
        Ratio ratio = new Ratio(value);
        assertEquals(value, ratio.ratio());
    }
}
