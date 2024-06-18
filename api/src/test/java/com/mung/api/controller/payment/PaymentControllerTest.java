package com.mung.api.controller.payment;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mung.api.controller.MockMember;
import com.mung.common.domain.PaymentMethod;
import com.mung.member.domain.Role;
import com.mung.payment.dto.PaymentDto.KaKaoCompletePaymentRequest;
import com.mung.payment.dto.PaymentDto.KaKaoCompletePaymentRequest.Amount;
import com.mung.payment.dto.PaymentDto.KaKaoCompletePaymentRequest.CardInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class PaymentControllerTest {

    @Autowired
    PaymentController paymentController;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @MockMember(id = 1L, name = "USER", role = Role.USER)
    public void 카카오결제_성공() throws Exception {
        // given
        KaKaoCompletePaymentRequest request = KaKaoCompletePaymentRequest.builder()
            .aid("1234567890")
            .tid("1234567890")
            .partnerUserId("1")
            .partnerOrderId("8")
            .amount(Amount.builder().total(3900).build())
            .cardInfo(CardInfo.builder().build())
            .paymentMethodType(PaymentMethod.CARD.name())
            .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/payment/kakao/complete")
                .contentType(APPLICATION_JSON)
                .content(json)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
            .andExpect(jsonPath("$.data.orderId").exists())
            .andDo(print());
    }

    @Test
    @MockMember(id = 1L, name = "USER", role = Role.USER)
    public void 카카오결제_실패_결제금액불일치() throws Exception {
        // given
        KaKaoCompletePaymentRequest request = KaKaoCompletePaymentRequest.builder()
            .aid("1234567890")
            .tid("1234567890")
            .partnerUserId("1")
            .partnerOrderId("8")
            .amount(Amount.builder().total(1000).build())
            .cardInfo(CardInfo.builder().build())
            .paymentMethodType(PaymentMethod.CARD.name())
            .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/payment/kakao/complete")
                .contentType(APPLICATION_JSON)
                .content(json)
            )
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$.message").value(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()))
            .andExpect(jsonPath("$.data.message").exists())
            .andDo(print());
    }

    @Test
    @MockMember(id = 1L, name = "USER", role = Role.USER)
    public void 카카오결제_실패_회원불일치() throws Exception {
        // given
        KaKaoCompletePaymentRequest request = KaKaoCompletePaymentRequest.builder()
            .aid("1234567890")
            .tid("1234567890")
            .partnerUserId("2")
            .partnerOrderId("8")
            .amount(Amount.builder().total(3900).build())
            .cardInfo(CardInfo.builder().build())
            .paymentMethodType(PaymentMethod.CARD.name())
            .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/payment/kakao/complete")
                .contentType(APPLICATION_JSON)
                .content(json)
            )
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$.message").value(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()))
            .andExpect(jsonPath("$.data.message").exists())
            .andDo(print());
    }

}