package com.ege.wooda.domain.member.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import com.ege.wooda.domain.member.dto.request.AuthRequest;
import com.ege.wooda.global.docs.RestDocsTest;
import com.ege.wooda.global.security.jwt.dto.JwtToken;
import com.ege.wooda.global.security.jwt.service.JwtGenerateService;
import com.fasterxml.jackson.core.JsonProcessingException;

import static com.ege.wooda.global.docs.ApiDocumentUtils.getDocumentRequest;
import static com.ege.wooda.global.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
public class MemberControllerTest extends RestDocsTest {
    @MockBean
    private JwtGenerateService jwtGenerateService;

    @Test
    @DisplayName("토큰 발급에 성공한다.")
    void getAuthToken() throws Exception {
        // given
        JwtToken mockToken = new JwtToken("access", "refresh", "Bearer");

        given(jwtGenerateService.createJwtTokenByAuthToken(anyString()))
                .willReturn(mockToken);

        ResultActions perform =
                mockMvc.perform(post("/api/v1/auth")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(toJson(new AuthRequest("auth"))));

        perform.andExpect(status().isOk())
               .andExpect(jsonPath("$.data.accessToken").value(mockToken.accessToken()))
               .andExpect(jsonPath("$.data.refreshToken").value(mockToken.refreshToken()));

        perform.andDo(print())
               .andDo(document("auth",
                               getDocumentRequest(),
                               getDocumentResponse(),
                               requestFields(
                                       fieldWithPath("authToken").type(JsonFieldType.STRING).description("인증 토큰")),
                               responseFields(
                                       fieldWithPath("status").type(JsonFieldType.STRING).description("응답 코드"),
                                       fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                       fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                       fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("엑세스 토큰"),
                                       fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰"))));
    }
}
