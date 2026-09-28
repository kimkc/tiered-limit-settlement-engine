package com.tieredlimit.domain.model;

public record Participant(String code, String brandCode) {
    public Participant{
        if(code == null || brandCode == null){
            throw new IllegalArgumentException("참여사 코드와 브랜드 코드는 null일 수 없습니다.");
        }
    }
}
