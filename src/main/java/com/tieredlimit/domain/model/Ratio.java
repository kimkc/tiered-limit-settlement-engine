package com.tieredlimit.domain.model;

public record Ratio(long ratio) {

    public Ratio {
        if (ratio < 0) {
            throw new IllegalArgumentException("분담율은 음수일 수 없습니다: " + ratio);
        }

        if (ratio > 10000){
            throw new IllegalArgumentException("분담율은 10000을 초과할 수 없습니다: " + ratio);
        }
    }
}
