package com.tieredlimit.adapter.in.web;

import com.tieredlimit.application.port.in.CalculateCommand;
import com.tieredlimit.domain.model.Money;
import com.tieredlimit.domain.model.Ratio;
import com.tieredlimit.domain.model.Tier;
import com.tieredlimit.domain.model.TierPolicy;
import com.tieredlimit.domain.model.Transaction;
import com.tieredlimit.domain.model.Apportionment;
import com.tieredlimit.domain.model.Settlement;
import com.tieredlimit.domain.model.ShareRatio;
import com.tieredlimit.domain.model.Participant;

import java.util.List;

public record CalculateRequest(TransactionDto transaction, TierPolicyDto tierPolicy) {
    public record TransactionDto(long originalAmount, long discountAmount, long approvedAmount){
        private Transaction toTransaction(){
            return new Transaction(new Money(this.originalAmount), new Money(this.discountAmount), new Money(this.approvedAmount));
        }
    }

    public record TierPolicyDto(long totalRatio, String participantCode, String brandCode, String cardCompanyCode,
                                String cardType, List<TierDto> tiers){
        private TierPolicy toTierPolicy(){
            return new TierPolicy(new Ratio(this.totalRatio), new Participant(this.participantCode, this.brandCode),
                    this.cardCompanyCode, this.cardType, this.tiers.stream().map(TierDto::toTier).toList());
        }
    }
    public record TierDto(int tierNo, long limitAmount, long cardRatio, long participantRatio, long companyRatio) {
        private Tier toTier(){
            return new Tier(this.tierNo, new Money(this.limitAmount), new ShareRatio(new Ratio(this.cardRatio), new Ratio(this.participantRatio), new Ratio(this.companyRatio)));
        }
    }

    public CalculateCommand toCommand(){
        List<Apportionment> apportionments = List.of();
        return new CalculateCommand(new Settlement(this.transaction().toTransaction(), apportionments), this.tierPolicy.toTierPolicy());
    }
}
