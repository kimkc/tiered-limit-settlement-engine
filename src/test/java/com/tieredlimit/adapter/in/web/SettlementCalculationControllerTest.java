package com.tieredlimit.adapter.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SettlementCalculationControllerTest {

    @Autowired
    MockMvc mockMvc;


    @Test
    @DisplayName("결제 및 정책 데이터 요청이 오면, 분담금과 할인금액을 계산하여 응답한다. 할인율 50%")
    void 결제_및_정책_데이터_요청이_오면_분담금과_할인금액을_계산하여_응답한다_할인율50() throws Exception {
        String requestJson = """
                {
                  "transaction": {
                    "originalAmount": 500000,
                    "discountAmount": 250000,
                    "approvedAmount": 250000
                  },
                  "tierPolicy": {
                    "totalRatio": 5000,
                    "participantCode": "1234",
                    "brandCode": "5678",
                    "cardCompanyCode": "10",
                    "cardType": "2",
                    "tiers": [
                      { "tierNo": 1, "limitAmount": 300000, "cardRatio": 1500, "participantRatio": 2000, "companyRatio": 1500 },
                      { "tierNo": 2, "limitAmount": 400000, "cardRatio": 0,    "participantRatio": 2500, "companyRatio": 2500 },
                      { "tierNo": 3, "limitAmount": 600000, "cardRatio": 0,    "participantRatio": 2000, "companyRatio": 3000 }
                    ]
                  }
                }
                """;

        mockMvc.    perform(post("/settlements/calculate")                    // ① 어떤 요청을
                        .contentType(MediaType.APPLICATION_JSON)   // ② Content-Type 헤더
                        .content(requestJson))                     // ③ 바디 (String)
                //.andDo(print())                         // ⑥ 요청·응답 전체 출력 (디버깅용)
                .andExpect(status().isOk())                    // ④ 기대: 상태코드
                .andExpect(jsonPath("$.totalDiscountAmount").value(250000))     // ⑤ 기대: 바디 내용
                .andExpect(jsonPath("$.apportionments.length()").value(3))
                .andExpect(jsonPath("$.apportionments[?(@.type=='CARD')].amount").value(45000))
                .andExpect(jsonPath("$.apportionments[?(@.type=='PARTICIPANT')].amount").value(105000))
                .andExpect(jsonPath("$.apportionments[?(@.type=='COMPANY')].amount").value(100000));

    }

    @Test
    @DisplayName("결제 및 정책 데이터 요청이 오면, 분담금과 할인금액을 계산하여 응답한다_할인율 30%.")
    void 결제_및_정책_데이터_요청이_오면_분담금과_할인금액을_계산하여_응답한다_할인율30() throws Exception {
        String requestJson = """
                {
                  "transaction": {
                    "originalAmount": 500000,
                    "discountAmount": 150000,
                    "approvedAmount": 350000
                  },
                  "tierPolicy": {
                    "totalRatio": 3000,
                    "participantCode": "1234",
                    "brandCode": "5678",
                    "cardCompanyCode": "10",
                    "cardType": "2",
                    "tiers": [
                      { "tierNo": 2, "limitAmount": 400000, "cardRatio": 0,    "participantRatio": 1500, "companyRatio": 1500 },
                      { "tierNo": 1, "limitAmount": 300000, "cardRatio": 500, "participantRatio": 500, "companyRatio": 2000 },
                      { "tierNo": 3, "limitAmount": 600000, "cardRatio": 0,    "participantRatio": 1000, "companyRatio": 2000 }
                    ]
                  }
                }
                """;

        mockMvc.    perform(post("/settlements/calculate")                    // ① 어떤 요청을
                        .contentType(MediaType.APPLICATION_JSON)   // ② Content-Type 헤더
                        .content(requestJson))                     // ③ 바디 (String)
                //.andDo(print())                         // ⑥ 요청·응답 전체 출력 (디버깅용)
                .andExpect(status().isOk())                    // ④ 기대: 상태코드
                .andExpect(jsonPath("$.totalDiscountAmount").value(150000))     // ⑤ 기대: 바디 내용
                .andExpect(jsonPath("$.apportionments.length()").value(3))
                .andExpect(jsonPath("$.apportionments[?(@.type=='CARD')].amount").value(15000))
                .andExpect(jsonPath("$.apportionments[?(@.type=='PARTICIPANT')].amount").value(40000))
                .andExpect(jsonPath("$.apportionments[?(@.type=='COMPANY')].amount").value(95000));

    }
}
