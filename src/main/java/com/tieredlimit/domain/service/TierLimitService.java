package com.tieredlimit.domain.service;

import com.tieredlimit.domain.model.*;

import java.util.ArrayList;
import java.util.List;

public class TierLimitService {

    /*
    1. 대상 결제건 추출 (v1 범위 밖 - 대상은 미리 지정되어 넘어옴)
    2. 거래 존재 및 금액 검증 (v1 범위 밖)
    3. 금액 계산 (이 메서드)
     */
    public List<Apportionment> calculateTierLimitApportionment(Settlement settlement, TierPolicy tierPolicy) {
        List<Apportionment> apportionments = new ArrayList<>();
        final int TOLERANCE = tierPolicy.getTiers().size() * 3; // (차수×party(ratio) 최대 내림 손실) 같은 유도값
        
        long tierDiscount = 0; //이 구간에 할당할 할인액
        long cardShare = 0; //카드사 분담누적
        long participantShare = 0; //참여사 분담누적
        long companyShare = 0; //소속사 분담누적
        long totalRatio = 0; //이 구간의 분담율 합
        
        long coveredAmount = 0; //직전 차수까지의 한도누적
        long tierLimit = 0; //이 차수의 유효 한도
        long bandAmount = 0; //이 차수의 구간폭
        
        
        long originalAmount = settlement.getTransaction().originalAmount().value();
        long discount = settlement.getTransaction().discountAmount().value();

        long remainingDiscount = discount; //남은 할인액

        if(remainingDiscount > 0 ){ //기존 and조건으로 승인건일 경우 있어야하긴함.
            //원래 3회로 고정시켜놓고, 정산테이블 브랜드, 카드사, 임직원 유형으로 한도증액 정책 조회해옴. 여기선 조회된 상태로 생각.
            //1. 1차 -> 2차 -> n차
            for (Tier tier : tierPolicy.getTiers()){
                long cardRatio = tier.shareRatio().card().ratio();
                long partRatio = tier.shareRatio().participant().ratio();
                long comRatio = tier.shareRatio().company().ratio();

                totalRatio = cardRatio + partRatio + comRatio;

                //2. 이 차수 구간폭 계산
                tierLimit = tier.limitAmount().value(); // 앞이 null이면 originalAmount;//현재 null일 수 없는듯?
                bandAmount = Math.min(tierLimit, originalAmount) - coveredAmount;
                
                //3. 구간폭에만 총 할인율 곱하고, 남은 remainingDiscount 한도 내로 cap
                tierDiscount = Math.min(bandAmount * totalRatio / 10000, remainingDiscount);
                
                //비율 합산 후 분배
                if(totalRatio > 0 && tierDiscount > 0){
                    cardShare = cardShare + tierDiscount * cardRatio / totalRatio;
                    participantShare = participantShare + tierDiscount * partRatio / totalRatio;
                    companyShare = companyShare + tierDiscount * comRatio / totalRatio;
                }
                
                //남은 할인액, 누적한도 차감
                remainingDiscount = remainingDiscount - tierDiscount;
                coveredAmount = coveredAmount + bandAmount;
            }
            
            //3차 한도 초과분(남은 할인액)은 전부 소속사로
            if (remainingDiscount > 0){
                companyShare = companyShare + remainingDiscount;
            }

            //계산된 분담금 3종의 합을 할인금액과 대조
            long sum = cardShare + participantShare + companyShare;
            if (Math.abs(sum - discount) > TOLERANCE) {   // TOLERANCE = 잔돈 허용치(예: 2원)
                throw new IllegalStateException("분담 합 " + sum + " ≠ 할인금액 " + discount);
            }

            //최종 금액 넘기기.
            companyShare += (discount - sum);

            //현재 할인분담금은 확인 하지 않음.
            //일반 할인분담금(카드사 + 참여사) 
            //임직원 할인분담금(소속사)
            
            //카드사분담금
            apportionments.add(new Apportionment(ApportionmentType.CARD, new Money(cardShare)));
            //참여사분담금
            apportionments.add(new Apportionment(ApportionmentType.PARTICIPANT, new Money(participantShare)));
            //소속사분담금
            apportionments.add(new Apportionment(ApportionmentType.COMPANY, new Money(companyShare)));
        }else{
            //할인금액이 음수일 경우 승인취소 원승인건 찾아서 그대로 -만 처리해줌.
            //현재 취소건 미구현, 의도된 공백
        }

        return apportionments;
    }
}