package com.tieredlimit.domain.model;

public record Tier(int tierNo, Money limitAmount, ShareRatio shareRatio) {

    public Tier {
        if (shareRatio == null || limitAmount == null){
            throw new IllegalArgumentException("분담률과 한도금액은 null일 수 없습니다");
        }

        if(tierNo <= 0){
            throw new IllegalArgumentException("차수는 0이거나 음수일 수 없습니다.");
        }

        if(limitAmount.value() <= 0){
            throw new IllegalArgumentException("한도금액은 0이거나 음수일 수 없습니다.");
        }
    }
}
