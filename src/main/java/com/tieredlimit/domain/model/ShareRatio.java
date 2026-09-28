package com.tieredlimit.domain.model;

public record ShareRatio(Ratio card, Ratio participant, Ratio company) {
    public ShareRatio {
        if (card == null || participant == null || company == null) {
            throw new IllegalArgumentException("분담율이 null일 수 없습니다");
        }
    }

    public long totalRatio(){
        return card.ratio() + participant.ratio() + company.ratio();
    }
}

