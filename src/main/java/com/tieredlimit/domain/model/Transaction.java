package com.tieredlimit.domain.model;

public record Transaction(Money originalAmount, Money discountAmount, Money approvedAmount) {
    //String memberId, String memberType, String franchiseNo,
    public Transaction{
        if (originalAmount == null || discountAmount == null || approvedAmount == null) {
            throw new IllegalArgumentException("결제 금액이 null일 수 없습니다");
        }
        if(originalAmount.value() - discountAmount.value() != approvedAmount.value())
            throw new IllegalArgumentException("할인전금액에서 할인금액을 빼면 항상 승인금액과 일치해야합니다.");
    }
}
