package com.tieredlimit.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MoneyTest {
    @Test
    @DisplayName("정상 값을 보존합니다.")
    void 정상값을_보존된다(){
        long value = 100;
        Money money = new Money(value);
        assertEquals(value, money.value());
    }

    @Test
    @DisplayName("0원은 허용된다")
    void 영원은_허용된다() {
        assertDoesNotThrow(() -> new Money(0));
    }

    @Test
    @DisplayName("취소 거래의 음수 금액은 허용된다")
    void 음수_금액은_허용된다() {
        assertDoesNotThrow(() -> new Money(-1));
    }

}
