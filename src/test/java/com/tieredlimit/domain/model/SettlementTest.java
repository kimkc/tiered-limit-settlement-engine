package com.tieredlimit.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SettlementTest {
    @Test
    @DisplayName("분담금 합이 할인금액과 같으면 통과한다.")
    void 분담금_합이_할인금액과_같으면_통과한다(){
        //given
        Transaction transaction = new Transaction(new Money(500_000), new Money(250_000), new Money(250_000));
        List<Apportionment> apportionments = new ArrayList<>();
        apportionments.add(new Apportionment(ApportionmentType.CARD, new Money(45_000)));
        apportionments.add(new Apportionment(ApportionmentType.PARTICIPANT, new Money(105_000)));
        apportionments.add(new Apportionment(ApportionmentType.COMPANY,  new Money(100_000)));

        Settlement settlement = new Settlement(transaction, apportionments);

        //when
        //then
        assertDoesNotThrow(() -> settlement.replaceApportionments(apportionments));
    }


    @ParameterizedTest(name = "[{index}] card={0}원, participant={1}원, company={2}원이면 할인금액은 {3}원이다")
    @MethodSource("provideMisMatchedApportionments")
    @DisplayName("분담금 합이 할인금액과 다르면 예외를 던진다-미달,초과")
    void 분담금_합이_할인금액과_다르면_예외를_던진다(Money card, Money participant, Money company, long discountAmount) {
        //given
        Transaction transaction = new Transaction(new Money(500_000), new Money(discountAmount), new Money(250_000));
        List<Apportionment> apportionments = new ArrayList<>();
        apportionments.add(new Apportionment(ApportionmentType.CARD, card));
        apportionments.add(new Apportionment(ApportionmentType.PARTICIPANT, participant));
        apportionments.add(new Apportionment(ApportionmentType.COMPANY,  company));

        Settlement settlement = new Settlement(transaction, apportionments);
        //when
        //then
       assertThrows(IllegalArgumentException.class,
                        () -> settlement.replaceApportionments(apportionments));
    }

    private static Stream<Arguments> provideMisMatchedApportionments() {
        return Stream.of(
                // 미달
                Arguments.of(new Money(45_000), new Money(100_000), new Money(100_000), 250_000L),
                // 초과
                Arguments.of(new Money(45_000), new Money(110_000), new Money(100_000), 250_000L)
                //Todo
                //음수(취소건)
                //Arguments.of(new Money(45_000), new Money(105_000), new Money(100_000), -250_000L)
        );
    }

    @Test
    @DisplayName("분담금이 없으면 할인금액이 0원이어야한다.")
    void 분담금이_없으면_할인금액이_0원이어야한다() {
        //given
        Transaction transaction = new Transaction(new Money(250_000), new Money(0), new Money(250_000));
        List<Apportionment> apportionments = new ArrayList<>();

        Settlement settlement = new Settlement(transaction, apportionments);
        //when
        //then
        assertDoesNotThrow(() -> settlement.replaceApportionments(apportionments));
    }

    @Test
    @DisplayName("할인금액이 0원 초과이고, 분담금이 없으면 예외를 던진다.")
    void 할인금액이_0원_초과이고_분담금이_없으면_예외를_던진다() {
        //given
        Transaction transaction = new Transaction(new Money(500_000), new Money(250_000), new Money(250_000));
        List<Apportionment> apportionments = new ArrayList<>();

        Settlement settlement = new Settlement(transaction, apportionments);
        //when
        //then
        assertThrows(IllegalArgumentException.class, () -> settlement.replaceApportionments(apportionments));
    }

    @Test
    @DisplayName("분담금을 교체합니다.")
    void 분담금을_교체합니다() {
        //given
        Transaction transaction = new Transaction(new Money(500_000), new Money(250_000), new Money(250_000));
        List<Apportionment> apportionments = new ArrayList<>();
        apportionments.add(new Apportionment(ApportionmentType.CARD, new Money(45_000)));
        apportionments.add(new Apportionment(ApportionmentType.PARTICIPANT, new Money(0)));
        apportionments.add(new Apportionment(ApportionmentType.COMPANY,  new Money(205_000)));

        Settlement settlement = new Settlement(transaction, apportionments);

        List<Apportionment> newApportionments = new ArrayList<>();
        newApportionments.add(new Apportionment(ApportionmentType.CARD, new Money(45_000)));
        newApportionments.add(new Apportionment(ApportionmentType.PARTICIPANT, new Money(105_000)));
        newApportionments.add(new Apportionment(ApportionmentType.COMPANY,  new Money(100_000)));

        //when
        settlement.replaceApportionments(newApportionments);

        //then
        assertThat(settlement.getApportionments()).containsExactlyElementsOf(newApportionments);
    }

    @Test
    @DisplayName("교체한 분담금이 다르면 예외를 던진다.")
    void 교체한_분담금이_다르면_예외를_던진다() {
        //given
        Transaction transaction = new Transaction(new Money(500_000), new Money(250_000), new Money(250_000));
        List<Apportionment> apportionments = new ArrayList<>();
        apportionments.add(new Apportionment(ApportionmentType.CARD, new Money(45_000)));
        apportionments.add(new Apportionment(ApportionmentType.PARTICIPANT, new Money(0)));
        apportionments.add(new Apportionment(ApportionmentType.COMPANY,  new Money(205_000)));

        Settlement settlement = new Settlement(transaction, apportionments);

        List<Apportionment> newApportionments = new ArrayList<>();
        newApportionments.add(new Apportionment(ApportionmentType.PARTICIPANT, new Money(105_000)));
        newApportionments.add(new Apportionment(ApportionmentType.COMPANY,  new Money(100_000)));

        //when
        //then
        assertThrows(IllegalArgumentException.class, () -> settlement.replaceApportionments(newApportionments));
        assertThat(settlement.getApportionments()).containsExactlyElementsOf(apportionments);

    }
}