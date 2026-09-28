package com.tieredlimit.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TransactionTest {

    @Test
    @DisplayName("할인전 금액에서 할인금액을 빼면 승인금액이어야한다.")
    void 할인전_금액에서_할인금액을_빼면_승인금액이어야한다(){
        //given
        //when
        //then
        assertDoesNotThrow(() -> new Transaction(new Money(500_000), new Money(250_000), new Money(250_000)));

    }

    @Test
    @DisplayName("할인 전 금액에서 할인금액을 뺀 금액이 승인금액과 다르면 예외를 던진다.")
    void 할인_전_금액에서_할인금액을_뺀_금액이_승인금액과_다르면_예외를_던진다(){
        //given
        //when
        //then
        assertThrows(IllegalArgumentException.class,
                () -> new Transaction(new Money(500_000), new Money(200_000), new Money(250_000)));

    }
}
