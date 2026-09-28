# 다단 한도 분담금 정산 엔진 (v1 데모)

> 결제 할인의 **분담금을 카드사 / 참여사 / 소속사 3자에게 누진 구조로 재계산**하는 엔진.
> 소득세 과세표준과 같은 방식으로, 할인전 금액이 차수(tier) 한도를 넘을 때마다 **구간별로 다른 분담율**을 적용한다.

Java 21 · Spring Boot 4.0.6 · 헥사고날 아키텍처 · TDD

> **v1 스코프 동결 — 2026-08-26.** 수직 슬라이스 DoD 4/4 완료.
> 여기서부터 기능을 더 붙이지 않는다. 새로 떠오르는 것은 전부 v1.1 이후로 미루고 문서에만 남긴다.
> 미뤄둔 것과 **일부러 그렇게 둔 것**의 구분은 [알려진 한계](docs/ARCHITECTURE.md#7-알려진-한계)에 있다.

📄 **설계 문서** — [아키텍처](docs/ARCHITECTURE.md) · [도메인 모델](docs/DOMAIN_MODEL.md) · [유비쿼터스 언어](docs/UBIQUITOUS_LANGUAGE.md) · [설계본(2026-06-03 스냅샷)](docs/design/class-diagram-v1.md)

---

## 문제

1차 정산이 끝난 시점에는 분담금이 **다단 한도를 적용하지 않은 채로** 생성되어 있다.
이 엔진은 그 결과를 받아 차수별 분담율로 다시 계산하고, 정산 상세를 **통째로 교체**한다(DELETE & INSERT).

정책은 `(참여사, 브랜드, 카드사, 카드종류)`로 식별되며, **차수 개수가 정책마다 다르다**(2차·3차 혼재).
따라서 차수는 `List<Tier>`이고, 1차/2차를 필드로 고정하지 않는다.

## 계산 예시

할인전 500,000원 / 총할인율 50% / 3차 정책

> 이 문서와 테스트에 나오는 한도·분담율·총할인율·코드값은 모두 설명을 위한 가상값이다.

| 차수 | 한도 | 적용 구간 | 구간 금액 | 카드사 | 참여사 | 소속사 |
|---|---|---|---|---|---|---|
| 1차 | 300,000 | 0 → 300,000 | 300,000 | 15% → **45,000** | 20% → **60,000** | 15% → **45,000** |
| 2차 | 400,000 | 300,000 → 400,000 | 100,000 | 0% → **0** | 25% → **25,000** | 25% → **25,000** |
| 3차 | 600,000 | 400,000 → 500,000 | 100,000 | 0% → **0** | 20% → **20,000** | 30% → **30,000** |
| | | | **합계** | **45,000** | **105,000** | **100,000** |

차수가 올라갈수록 카드사 분담이 줄고 소속사 분담이 늘어난다.
세 합은 250,000 = 500,000 × 50% — **총할인율은 구간이 몇 개로 쪼개지든 보존된다.**

## 실행

```bash
./gradlew bootRun
``` 

```bash
curl -X POST localhost:8080/settlements/calculate \
  -H "Content-Type: application/json" \
  -d '{"transaction":{"originalAmount":500000,"discountAmount":250000,"approvedAmount":250000},
       "tierPolicy":{"totalRatio":5000,"participantCode":"1234","brandCode":"5678",
       "cardCompanyCode":"10","cardType":"2","tiers":[
         {"tierNo":1,"limitAmount":300000,"cardRatio":1500,"participantRatio":2000,"companyRatio":1500},
         {"tierNo":2,"limitAmount":400000,"cardRatio":0,"participantRatio":2500,"companyRatio":2500},
         {"tierNo":3,"limitAmount":600000,"cardRatio":0,"participantRatio":2000,"companyRatio":3000}]}}'
```

```json
{
  "apportionments": [
    { "type": "CARD",        "amount": 45000  },
    { "type": "PARTICIPANT", "amount": 105000 },
    { "type": "COMPANY",     "amount": 100000 }
  ],
  "totalDiscountAmount": 250000
}
```

`totalDiscountAmount`는 분담금 합이 아니라 **거래의 할인금액에서 꺼낸 값**이다.
합으로 다시 계산하면 응답 안에서 항상 참인 동어반복이 되어, 클라이언트가 검산할 수단이 사라진다.

## 아키텍처

```
domain/         순수 Java. 프레임워크 의존 없음
  model/        Money · Ratio · ShareRatio · Tier · Participant · Transaction
                Apportionment · Settlement(AR) · TierPolicy(AR)
  service/      TierLimitService — 누진 계산만. 애그리거트를 수정하지 않는다
application/
  port/in/      SettlementCalculationUseCase · CalculateCommand · CalculateResult
  service/      오케스트레이션: 계산 → 애그리거트에 교체 요청
adapter/in/web/ CalculateRequest · CalculateResponse · Controller
```

**의존은 안쪽으로만 흐른다.** 어댑터가 도메인을 알고, 도메인은 DTO도 스프링도 모른다.
요청/응답 변환은 전부 어댑터가 소유한다 — `CalculateResponse.from(Settlement)`이지 `Settlement.toResponse()`가 아니다.

금액과 분담율은 `long`이다. 금액은 원 단위 정수, 분담율은 basis point(10000 = 100%).
`double`은 정산에서 쓰지 않고, `BigDecimal`은 원 단위 정수 도메인에 과하다.
3자 분배에서 생기는 잔돈은 **마지막 party가 흡수**해 합 불변식을 지킨다.

## 불변식이 사는 곳

| 규칙 | 위치 | 이유 |
|---|---|---|
| 할인전 − 할인 = 승인 | `Transaction` 생성자 | 변하지 않는 값 → 생성 시점에 막는다 |
| 차수 한도는 증가한다 | `TierPolicy` 생성자 | 애그리거트 루트가 자기 구성요소를 검증 |
| 모든 차수의 분담율 합 = 정책 총할인율 | `TierPolicy` 생성자 | 차수끼리만 비교하면 "전부 똑같이 틀린" 경우를 못 잡는다 |
| 분담금 합 = 할인금액 | `Settlement.replaceApportionments()` | 변하는 상태 → 상태를 바꾸는 메서드가 지킨다 |

마지막 규칙은 **인자를 검증한 뒤에 대입**한다. 대입 후 검증이면 예외가 나가도 상태는 이미 오염된 뒤다(실패 원자성).

한편 `Settlement` **생성자는 분담금을 검증하지 않는다.** 이 엔진의 존재 이유가 *틀린 1차 결과를 받아서 고치는 것*이라, 입구를 막으면 정작 고쳐야 할 대상을 받지 못한다. **입구는 넓게, 출구는 좁게.**

## 스코프 경계

- **v1 데모는 아웃바운드 포트(DB)를 스코프 아웃하여 정책을 요청 바디로 받는다. 실제 운영에서는 정책 키로 조회한다.** 이 API 모양은 데모의 제약이지 설계 의도가 아니다.
- 도메인 불변식 위반(`IllegalArgumentException`)이 현재 **HTTP 500**으로 나간다. 클라이언트 입력 오류이므로 400이 맞고, `@RestControllerAdvice` 매핑은 **v1.1로 미뤘다.**
- 다만 `TierLimitService`의 자기검산 실패(`IllegalStateException`)는 **500이 맞다 — 의도한 것이다.** 그건 엔진이 자기 계산이 틀렸다고 알리는 신호이고, 400으로 내리면 클라이언트에게 거짓말이 된다. 실제로 2026-08-26의 차수 정렬 버그가 이 경로로 드러났다.
- 취소 거래(음수 할인), 결제 승인 실시간 흐름, SAP/EAI 연동, 어드민 UI는 v1 범위 밖.

## 테스트

```bash
./gradlew test   # 43 tests
```

VO·애그리거트 단위 테스트와, HTTP 요청 JSON부터 응답 JSON까지 한 번에 통과시키는 통합 테스트 2개(총율 50% / 30%).
통합 테스트의 기대값은 **손으로 계산한 리터럴**이다 — 프로덕션 코드에서 뽑아 쓰면 테스트가 자기 자신을 증명하는 꼴이 된다.

총율이 다른 정책을 두 개 두는 이유가 있다. 총율이 정확히 50%면 `할인전 − 할인 = 승인`이라는 불변식 때문에
**`할인 = 승인`이 구조적으로 항상 참**이 되어, 응답의 `discountAmount`를 `approvedAmount`로 바꿔도 테스트가 통과한다.
30% 케이스가 그 항등을 깨서 이 결함을 잡는다.
