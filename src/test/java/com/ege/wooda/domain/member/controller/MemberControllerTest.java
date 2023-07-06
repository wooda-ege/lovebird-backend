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
import com.ege.wooda.global.security.oauth.ios.dto.AppleAuthRequest;
import com.ege.wooda.global.security.oauth.ios.dto.user.AppleUser;
import com.ege.wooda.global.security.oauth.ios.dto.user.Name;
import com.ege.wooda.global.security.oauth.ios.service.AppleAuthService;
import com.ege.wooda.global.security.oauth.model.PrincipalUser;
import com.fasterxml.jackson.core.JsonProcessingException;

import static com.ege.wooda.global.docs.ApiDocumentUtils.getDocumentRequest;
import static com.ege.wooda.global.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
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

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.naming.AuthenticationException;

@WebMvcTest(MemberController.class)
public class MemberControllerTest extends RestDocsTest {
    @MockBean
    private JwtGenerateService jwtGenerateService;

    @MockBean
    private AppleAuthService appleAuthService;

    @Test
    @DisplayName("토큰 발급에 성공한다.")
    void getAuthToken() throws Exception {
        // given
        JwtToken mockToken = getJwtToken();

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
                                       fieldWithPath("authToken").type(JsonFieldType.STRING)
                                                                 .description("인증 토큰")),
                               responseFields(
                                       fieldWithPath("status").type(JsonFieldType.STRING).description("응답 코드"),
                                       fieldWithPath("message").type(JsonFieldType.STRING)
                                                               .description("응답 메시지"),
                                       fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                       fieldWithPath("data.accessToken").type(JsonFieldType.STRING)
                                                                        .description("엑세스 토큰"),
                                       fieldWithPath("data.refreshToken").type(JsonFieldType.STRING)
                                                                         .description("리프레시 토큰"))));
    }

    @Test
    @DisplayName("애플 로그인 검증 후 토큰 발급에 성공한다.")
    void handleAppleAuthAndGetAuthToken()
            throws Exception {
        // given
        PrincipalUser mockUser = new PrincipalUser(null, null, null);
        JwtToken mockToken = getJwtToken();

        given(appleAuthService.loadUser(any()))
                .willReturn(mockUser);
        given(jwtGenerateService.createJwtTokenByAppleAuth(any()))
                .willReturn(mockToken);

        // when
        AppleAuthRequest appleAuthRequest = new AppleAuthRequest("vuq3va.bir5h2p1.bi9qk1uib",
                                                                 new AppleUser("test@apple.com",
                                                                               new Name("Beom", "Kwak")));

        ResultActions perform = mockMvc.perform(post("/api/v1/auth/apple")
                                                        .contentType(MediaType.APPLICATION_JSON)
                                                        .content(toJson(appleAuthRequest)));
        // then
        perform.andExpect(status().isOk())
               .andExpect(jsonPath("$.data.accessToken").value(mockToken.accessToken()))
               .andExpect(jsonPath("$.data.refreshToken").value(mockToken.refreshToken()));

        perform.andDo(print())
               .andDo(document("auth-apple",
                               getDocumentRequest(),
                               getDocumentResponse(),
                               requestFields(
                                       fieldWithPath("identityToken").type(JsonFieldType.STRING)
                                                                     .description("Apple Identity Token"),
                                       fieldWithPath("user").type(JsonFieldType.OBJECT)
                                                            .description("Apple User"),
                                       fieldWithPath("user.email").type(JsonFieldType.STRING)
                                                                  .description("User Email"),
                                       fieldWithPath("user.name").type(JsonFieldType.OBJECT)
                                                                 .description("User name"),
                                       fieldWithPath("user.name.firstName").type(JsonFieldType.STRING)
                                                                           .description("User 이름"),
                                       fieldWithPath("user.name.lastName").type(JsonFieldType.STRING)
                                                                          .description("User 성")),
                               responseFields(
                                       fieldWithPath("status").type(JsonFieldType.STRING).description("응답 코드"),
                                       fieldWithPath("message").type(JsonFieldType.STRING)
                                                               .description("응답 메시지"),
                                       fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                       fieldWithPath("data.accessToken").type(JsonFieldType.STRING)
                                                                        .description("엑세스 토큰"),
                                       fieldWithPath("data.refreshToken").type(JsonFieldType.STRING)
                                                                         .description("리프레시 토큰"))));
    }

    private JwtToken getJwtToken() {
        return new JwtToken("access", "refresh", "Bearer");
    }
}
