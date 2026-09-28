# 유비쿼터스 언어 — 다단 한도 분담금 정산 엔진

> 업무 용어(한국어) ↔ 코드 이름(영어)의 **단일 기준**.
> 출처: 작성자가 개인적으로 정리한 용어집 시트 (2026-08-26 반영) + 당시 코드 대조
>
> **이 문서의 목적은 "회의에서 쓰는 말과 코드에 적힌 말이 같은가"를 계속 확인하는 것이다.**
> 둘이 갈라지면 코드를 읽을 때마다 번역이 필요해지고, 번역이 필요한 순간 오해가 들어온다.

---

## 1. 확정 용어

| 한국어 (업무) | 코드 이름 | 어디에 있나 | 결정 이유 |
|---|---|---|---|
| 정산 (테이블) | `Settlement` | `domain/model/Settlement.java` «AR» | |
| 정산 상세 (테이블) | `Apportionment` | `domain/model/Apportionment.java` | **분담금 위주**이기 때문. |
| 분담금 | `Apportionment` | 위와 같음 | `Share`보다 정산 도메인의 뜻이 분명하다 |
| 참여사 | `Participant` | `domain/model/Participant.java` | `agency`는 대리점으로 읽힐 여지가 있다 |
| 브랜드 | `brandCode` | `Participant.brandCode` | 참여사와 **항상 같이 다니는 키 묶음** |
| 할인전 금액 | `originalAmount` | `Transaction` | `realAmount`는 "진짜 금액"으로 읽혀 모호하다 |
| 할인 금액 | `discountAmount` | `Transaction` | |
| 승인 금액 | `approvedAmount` | `Transaction` | `payAmount`는 결제 시점과 혼동된다 |
| 다단 한도 | `TieredLimit` | 패키지 `com.tieredlimit` / 레포명 | `MultiStep`보다 누진(과세표준) 뉘앙스가 산다 → **⚠️ 클래스명은 어긋나 있다. [3장](#3-어긋난-곳) 참고** |
| 한도 금액 | `limitAmount` | `Tier.limitAmount` | |
| 차수 | `Tier` | `domain/model/Tier.java` | `Step`·`Level`보다 세율 구간의 뜻에 가깝다 |
| 차수 번호 | `tierNo` | `Tier.tierNo` | **순서의 단일 진실 원천.** 리스트 인덱스로 대체하지 않는다 |
| 다단 정책 | `TierPolicy` | `domain/model/TierPolicy.java` «AR» | |
| 금액 | `amount` | `Apportionment.amount` | → **⚠️ `Money`의 필드는 `value`다. [3장](#3-어긋난-곳)** |
| 분담 유형 | `ApportionmentType` | enum `CARD`/`PARTICIPANT`/`COMPANY` | `ratioType`은 비율의 종류로 오해된다 |
| 카드사 분담율 | `cardRatio` | `CalculateRequest.TierDto` (JSON) | → **⚠️ 도메인 VO는 `card`. [3장](#3-어긋난-곳)** |
| 참여사 분담율 | `participantRatio` | 〃 | 〃 |
| 소속사 분담율 | `companyRatio` | 〃 | 〃 |
| 회원 유형 (일반/임직원) | `memberType` | **미구현** | v1 스코프 밖 |
| 카드 회원번호 | `memberId` | **미구현** | v1 스코프 밖 |
| 정산 키 | `settlementId` | **미구현** | 정산 키가 필요하고, **트랜잭션의 키와는 다르다.** 루트 애그리거트에 붙인다 |

---

## 2. 코드에는 있는데 용어집에 없는 말

코드를 읽다 만나는데 시트에 정의가 없는 것들이다. **용어집에 추가할 후보**다.

| 코드 이름 | 뜻 | 왜 중요한가 |
|---|---|---|
| `totalRatio` | **총할인율** — 정책 전체의 할인율 | 한 가지가 아니고(예: 2000·3000·5000) **브랜드마다 다를 수 있다.** 정책 테이블의 컬럼이고, 차수 간 일관성 불변식의 기준값이다 |
| `cardType` | **카드 종류** — 정책 키의 일부. 값은 정책 테이블이 정의한다 | **다단 한도 대상은 일부 카드 종류뿐**이다 |
| `cardCompanyCode` | **카드사** 코드 | 정책 키의 일부 |
| `Money` | 원 단위 금액 래퍼 | 타입 이름이지 업무 용어는 아니지만, 코드 전반에 나온다 |
| `Ratio` | 분담율 1개. **basis point**(10000 = 100%) | "50%"를 코드에서 `5000`으로 쓰는 규칙 자체가 팀 합의 사항이다 |
| `ShareRatio` | 한 차수의 3자 분담율 묶음 | |
| `Transaction` | 거래 — 금액 3종 스냅샷 | 시트의 "정산 키" 설명에 나오는 *트랜잭션*과 같은 말인지 확인 필요 |
| `Settlement.replaceApportionments` | **재처리** = DELETE & INSERT | "재처리"라는 업무 용어가 코드에서 이 메서드 하나로 표현된다 |
| `CARD` / `PARTICIPANT` / `COMPANY` | 카드사 / 참여사 / **소속사** | **"소속사"의 영어가 `COMPANY`**라는 게 시트에 없다. `company`는 일반명사라 오해 여지가 크다 |

---

## 3. 어긋난 곳

**용어집과 코드가 다른 지점 5개.** 아직 아무것도 고치지 않았다 — v1이 동결 상태이고, 어느 쪽을 진실로 삼을지는 결정 사항이기 때문이다.

### ① `TieredLimit` vs `TierLimit`

| 위치 | 현재 이름 |
|---|---|
| 패키지 | `com.tieredlimit` ✅ 시트와 일치 |
| 레포명 | `tiered-limit-settlement-engine` ✅ 일치 |
| **클래스** | `TierLimitService` ❌ |
| **메서드** | `calculateTierLimitApportionment()` ❌ |

패키지와 레포는 `tiered`인데 클래스와 메서드만 `tier`다. 같은 개념이 한 프로젝트 안에서 두 철자로 나타난다.

> 참고: `Tier`(차수)와 `TieredLimit`(다단 한도)은 **다른 개념**이다. `TierPolicy`는 "차수들의 정책"이라 `Tier`가 맞지만,
> `TierLimitService`는 "다단 한도 계산"이므로 `TieredLimitService`가 맞다.

### ② 분담율 필드명 — `cardRatio` vs `card`

| 레이어 | 이름 |
|---|---|
| JSON API / DTO (`TierDto`) | `cardRatio` · `participantRatio` · `companyRatio` ✅ 시트와 일치 |
| **도메인 VO (`ShareRatio`)** | `card` · `participant` · `company` ❌ |

2026-06-03에 **"타입이 이미 `Ratio`라 접미사가 중복"**이라는 이유로 짧은 이름을 택했다. 시트 결정은 반대다.

- **짧은 이름이 이기는 조건**: `shareRatio.card()`처럼 **문맥이 타입에 이미 있을 때**. `shareRatio.cardRatio()`는 ratio가 두 번 나온다.
- **긴 이름이 이기는 조건**: 필드를 **문맥 밖으로 꺼내 쓸 때**. 실제로 `TierLimitService`에서 `long cardRatio = tier.shareRatio().card().ratio();`처럼 **꺼내면서 다시 긴 이름을 붙이고 있다** — 짧은 이름이 필요한 곳까지 짧지는 않다는 증거다.

### ③ `settlementId` — 코드에 없다

시트: *"정산 키값 필요, 트랜잭션의 키와 다름. 루트 애그리거트에 적용"*

이건 **설계본과 다른 결정**이다. `design/class-diagram-v1.md`에는 `TransactionKey`(카드사 거래를 식별하는 키 조합) VO가 루트에 붙어 있었다.
시트는 **트랜잭션 키와 구별되는 정산 자체의 키**를 말한다. 둘의 관계가 정리돼야 한다.

### ④ `Money.value` vs `amount`

시트는 "금액 = `amount`"다. `Apportionment.amount`는 맞지만, `Money`의 필드는 `value`다 (`money.value()`).
설계본에서는 `Money.won`이었고 구현하면서 `value`가 됐다 — **두 번 바뀌는 동안 시트와 한 번도 맞은 적이 없다.**

### ⑤ `Apportionment.type` vs `apportionmentType`

시트 결정은 `apportionmentType`, 코드는 `type`이다.
②와 같은 성격(문맥이 타입에 있으니 접두사 생략)이므로 **같은 원칙으로 함께 결정**하는 게 맞다.

---

## 4. 명명 원칙 (위 결정들에서 뽑아낸 것)

1. **타입이 이미 말해주는 것을 필드명에 반복하지 않는다** — 다만 값을 지역변수로 꺼내는 순간 문맥이 사라지므로, 그때는 긴 이름을 쓴다. (②의 논점)
2. **숫자가 아니라 관계를 이름에 담는다** — `totalRatio`는 "5000"이 아니라 "정책이 선언한 총율"이다.
3. **업무에서 구별하는 것은 코드에서도 구별한다** — 정산 키 ≠ 트랜잭션 키(③), 차수 ≠ 다단 한도(①).
4. **일반명사는 위험하다** — `company`(소속사), `value`(금액), `type`(분담 유형)처럼 뜻이 넓은 단어는 문맥이 사라지면 오독된다.

---

## 5. 다음에 결정할 것

- [ ] ①~⑤를 **어느 쪽으로 통일할지** (시트를 코드에 맞출 것인가, 코드를 시트에 맞출 것인가)
- [ ] 2장의 용어들(`totalRatio` · `cardType` · **소속사=`COMPANY`** 등)을 시트에 추가
- [ ] `settlementId`와 `TransactionKey`의 관계 — 둘 다 필요한가, 하나로 합치나
- [ ] `memberType` 축 문제 — 카드 종류 축과 회원유형 축이 중복될 수 있다. **1축인가 2축(회원구분 × 카드구분)인가**
