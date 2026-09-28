package com.tieredlimit.adapter.in.web;

import com.tieredlimit.domain.model.Apportionment;
import com.tieredlimit.domain.model.ApportionmentType;
import com.tieredlimit.domain.model.Settlement;

import java.util.List;

public record CalculateResponse(List<ApportionmentDto> apportionments, long totalDiscountAmount) {

    public record ApportionmentDto(ApportionmentType type, long amount) {
        public static ApportionmentDto from(Apportionment apportionment) {
            return new ApportionmentDto(apportionment.type(), apportionment.amount().value());
        }
    }

    public static CalculateResponse from(Settlement settlement) {
        return new CalculateResponse(settlement.getApportionments().stream().map(ApportionmentDto::from).toList(), settlement.getTransaction().discountAmount().value());
    }


}
