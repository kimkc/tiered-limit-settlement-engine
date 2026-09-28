package com.tieredlimit.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantTest {
    @Test
    @DisplayName("참여사 코드는 null일 수 없습니다. ")
    void 참여사_코드는_null일_수_없습니다() {
        assertThrows(IllegalArgumentException.class, () ->
                new Participant(null, "5678")
        );
    }

    @Test
    @DisplayName("브랜드 코드는 null일 수 없습니다. ")
    void 브랜드_코드는_null일_수_없습니다() {
        assertThrows(IllegalArgumentException.class, () ->
                new Participant("1234", null)
        );
    }
}
