package com.ege.wooda.domain.couple.controller;

import com.ege.wooda.domain.couple.domain.Couple;
import com.ege.wooda.domain.couple.dto.request.CoupleLinkRequest;
import com.ege.wooda.domain.couple.service.CoupleService;
import com.ege.wooda.domain.member.domain.Member;
import com.ege.wooda.domain.member.domain.Oauth2Entity;
import com.ege.wooda.domain.member.domain.enums.Gender;
import com.ege.wooda.domain.member.service.MemberService;
import com.ege.wooda.domain.profile.domain.Profile;
import com.ege.wooda.domain.profile.service.ProfileService;
import com.ege.wooda.global.firebase.service.FCMService;
import com.ege.wooda.global.security.jwt.util.JwtValidator;
import com.ege.wooda.global.security.oauth.model.enums.SocialProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.ege.wooda.global.docs.ApiDocumentUtils.getDocumentRequest;
import static com.ege.wooda.global.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(CoupleController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
public class CoupleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CoupleService coupleService;

    @MockBean
    private MemberService memberService;

    @MockBean
    private ProfileService profileService;

    @MockBean
    private JwtValidator jwtValidator;

    @MockBean
    private FCMService fcmService;

    @Test
    @DisplayName("연동 코드를 발급 받는다.")
    public void getCoupleCode() throws Exception{
        Couple couple=getCouple(coupleService.generateCode(), 3L);
        Couple partner=getCouple(coupleService.generateCode(), 6L);

        ReflectionTestUtils.setField(partner, "id", 1L);
        ReflectionTestUtils.setField(partner, "coupleCode", "R5gh7dk0");
        ReflectionTestUtils.setField(partner, "useCode", Boolean.valueOf("false"));
        ReflectionTestUtils.setField(partner.getAuditEntity(), "createdAt",
                getLocalDateTime("2023-07-01T19:02:14"));
        ReflectionTestUtils.setField(partner.getAuditEntity(), "updatedAt",
                getLocalDateTime("2023-07-01T19:02:14"));

        given(coupleService.findByMemberId(6L))
                .willReturn(partner);
        given(coupleService.getCoupleCode(6L))
                .willReturn(partner);

        System.out.println(partner.getMemberId());
        System.out.println(partner.getCoupleCode());

        ResultActions resultActions=this.mockMvc.perform(
                get("/api/v1/{id}/couple/code", 6L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"));

        resultActions.andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andDo(document("couple-getCode",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(parameterWithName("id").description("일정 Id")),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.memberId").type(JsonFieldType.NUMBER)
                                        .description("Member Id"),
                                fieldWithPath("data.coupleCode").type(JsonFieldType.STRING).description("연동 코드")
                        )));
    }

    @Test
    @DisplayName("커플 연동을 시도한다.")
    public void link() throws Exception{
        Couple couple=getCouple(coupleService.generateCode(), 3L);
        Couple partner=getCouple(coupleService.generateCode(), 6L);

        Member gildong=getMember(getOauth2Entity(SocialProvider.KAKAO, "gildonghong", "홍길동", "gildong.hong@gmail.com"));

        Profile selfP = getProfile(3L, "홍길동", getLocalDate("2023-06-01"), Gender.MALE);
        Profile partnerP = getProfile(6L, "홍길순", getLocalDate("2023-06-01"), Gender.FEMALE);

        ReflectionTestUtils.setField(couple, "id", 1L);
        ReflectionTestUtils.setField(couple, "coupleCode", "t7Hq2s0g");
        ReflectionTestUtils.setField(couple, "useCode", Boolean.valueOf("false"));
        ReflectionTestUtils.setField(couple.getAuditEntity(), "createdAt",
                getLocalDateTime("2023-07-01T19:02:14"));
        ReflectionTestUtils.setField(couple.getAuditEntity(), "updatedAt",
                getLocalDateTime("2023-07-01T19:02:14"));

        gildong.registerDeviceToken("7sdkfjv3d");

        given(memberService.findById(anyLong()))
                .willReturn(gildong);
        given(profileService.findProfileByMemberId(3L))
                .willReturn(selfP);
        given(profileService.findProfileByMemberId(6L))
                .willReturn(partnerP);

        given(coupleService.findByMemberId(6L))
                .willReturn(partner);
        given(coupleService.getCoupleCode(3L))
                .willReturn(couple);

        CoupleLinkRequest coupleLinkRequest=CoupleLinkRequest.builder()
                .coupleCode("t7Hq2s0g")
                .build();

        String request=objectMapper.writeValueAsString(coupleLinkRequest);

        ResultActions resultActions=this.mockMvc.perform(
                multipart("/api/v1/{id}/couple/link",6L)
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        })
                        .content(request)
                        .contentType("application/json")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"));

        resultActions.andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andDo(document("couple-link",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(parameterWithName("id").description("Couple Id")),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data").description("응답 메시지")
                        )));
    }

    private Couple getCouple(String coupleCode, Long memberId){
        return Couple.builder()
                .memberId(memberId)
                .coupleCode(coupleCode)
                .build();
    }

    private Profile getProfile(Long memberId, String nickname, LocalDate firstDate, Gender gender) {
        return Profile.builder()
                .memberId(memberId)
                .nickname(nickname)
                .firstDate(firstDate)
                .gender(gender)
                .build();
    }

    private Member getMember(Oauth2Entity oauth2Entity){
        return Member.builder()
                .oauth2Entity(oauth2Entity)
                .build();
    }

    private Oauth2Entity getOauth2Entity(SocialProvider socialProvider, String accountId, String username, String email){
        return Oauth2Entity.builder()
                .socialProvider(SocialProvider.KAKAO)
                .accountId(accountId)
                .username(username)
                .email(email)
                .build();
    }

    private LocalDate getLocalDate(String memoryDate) {
        return LocalDate.parse(memoryDate, DateTimeFormatter.ISO_DATE);
    }

    private LocalDateTime getLocalDateTime(String date) {
        return LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

}
