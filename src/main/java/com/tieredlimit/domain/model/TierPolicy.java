package com.tieredlimit.domain.model;

import java.util.List;

import static java.util.Comparator.comparingInt;

public final class TierPolicy {
    private final Ratio totalRatio;
    private final Participant participant;
    private final String cardCompanyCode;
    private final String cardType;
    private final List<Tier> tiers;

    public TierPolicy(Ratio totalRatio, Participant participant, String cardCompanyCode, String cardType, List<Tier> tierPolicies){
        this.totalRatio = totalRatio;
        this.participant = participant;
        this.cardCompanyCode = cardCompanyCode;
        this.cardType = cardType;
        this.tiers = List.copyOf(tierPolicies.stream().sorted(comparingInt(Tier::tierNo)).toList());

        validateTierLimitOrder();
        validateTotalRatio();

    }

    private void validateTierLimitOrder(){
       long beforeLimitAmount = 0;

        for(Tier tier : tiers){
            if(beforeLimitAmount >= tier.limitAmount().value()){
                throw new IllegalArgumentException("차수한도는 이전 차수보다 커야합니다.");
            }
            beforeLimitAmount = tier.limitAmount().value();
        }
    }

    private void validateTotalRatio(){
        for(int i = 0; i < tiers.size(); i++){
            if(this.totalRatio.ratio() != tiers.get(i).shareRatio().totalRatio()){
                throw new IllegalArgumentException(String.format("차수마다 총 할인율은 동일해야한다. 정책 총 할인율: %d, %d차 총할인율: %d", this.totalRatio.ratio(), tiers.get(i).tierNo(), tiers.get(i).shareRatio().totalRatio()));
            }
        }
    }

    public Ratio getTotalRatio() {
        return totalRatio;
    }

    public Participant getParticipant() {
        return participant;
    }

    public String getCardCompanyCode() {
        return cardCompanyCode;
    }

    public String getCardType() {
        return cardType;
    }

    public List<Tier> getTiers() {
        return tiers;
    }
}
