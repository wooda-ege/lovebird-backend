package com.ege.wooda.domain.profile.controller;

import com.ege.wooda.domain.member.domain.enums.Gender;
import com.ege.wooda.domain.profile.domain.Profile;
import com.ege.wooda.domain.profile.dto.request.ProfileCreateRequest;
import com.ege.wooda.domain.profile.dto.request.ProfileUpdateRequest;
import com.ege.wooda.domain.profile.service.ProfileService;
import com.ege.wooda.global.docs.RestDocsTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.ege.wooda.global.docs.ApiDocumentUtils.getDocumentRequest;
import static com.ege.wooda.global.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfileController.class)
class ProfileControllerTest extends RestDocsTest {
    @MockBean
    private ProfileService profileService;

    @Test
    @DisplayName("Profile를 생성한다.")
    void add() throws Exception {
        // given
        Long mockId = 1L;
        MockMultipartFile mockImage = getMultipartFile();
        Profile mockProfile = getProfile(mockId, "홍길동", getLocalDate("2023-05-15"), Gender.MALE);

        given(profileService.save(any()))
                .willReturn(mockProfile);

        // when
        ProfileCreateRequest profileCreateRequest = new ProfileCreateRequest("홍길동", "2023-05-15", "MALE");
        String contents = objectMapper.writeValueAsString(profileCreateRequest);

        ResultActions result = this.mockMvc.perform(
                multipart("/api/v1/{id}/profile", mockId)
                        .file(mockImage)
                        .file(new MockMultipartFile("profileCreateRequest", "", "application/json",
                                                    contents.getBytes(StandardCharsets.UTF_8)))
                        .contentType("multipart/form-data")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"));

        // then
        result.andExpect(status().isCreated())
              .andDo(print())
              .andDo(document("profile-add",
                              getDocumentRequest(),
                              getDocumentResponse(),
                              pathParameters(
                                      parameterWithName("id").description("Member ID")
                              ),
                              requestPartBody("image"),
                              requestPartFields("profileCreateRequest",
                                                fieldWithPath("nickname").type(JsonFieldType.STRING)
                                                                         .description("닉네임"),
                                                fieldWithPath("firstDate").type(JsonFieldType.STRING)
                                                                          .description("사귄 날짜"),
                                                fieldWithPath("gender").type(JsonFieldType.STRING)
                                                                       .description("성별")
                              ),
                              responseFields(
                                      fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                                      fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                      fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                      fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("Member ID"),
                                      fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("별칭"),
                                      fieldWithPath("data.gender").type(JsonFieldType.STRING).description("성별"),
                                      fieldWithPath("data.profileImageUrl").type(JsonFieldType.STRING).description("프로필 이미지 URL").optional(),
                                      fieldWithPath("data.linkedFlag").type(JsonFieldType.BOOLEAN).description("커플 연동 여부"),
                                      fieldWithPath("data.anniversaryList").type(JsonFieldType.OBJECT).description("기념일 정보"),
                                      fieldWithPath("data.anniversaryList.FIRST_DATE").type(JsonFieldType.STRING).description("사귄 날짜"),
                                      fieldWithPath("data.anniversaryList.ONE_HUNDRED").type(JsonFieldType.STRING).description("백일"),
                                      fieldWithPath("data.anniversaryList.TWO_HUNDREDS").type(JsonFieldType.STRING).description("이백일"),
                                      fieldWithPath("data.anniversaryList.THREE_HUNDREDS").type(JsonFieldType.STRING).description("삼백일"),
                                      fieldWithPath("data.anniversaryList.ONE_YEAR").type(JsonFieldType.STRING).description("1주년"),
                                      fieldWithPath("data.anniversaryList.TWO_YEARS").type(JsonFieldType.STRING).description("2주년"),
                                      fieldWithPath("data.anniversaryList.THREE_YEARS").type(JsonFieldType.STRING).description("3주년"),
                                      fieldWithPath("data.anniversaryList.FOUR_YEARS").type(JsonFieldType.STRING).description("4주년"),
                                      fieldWithPath("data.anniversaryList.FIVE_YEARS").type(JsonFieldType.STRING).description("5주년"),
                                      fieldWithPath("data.anniversaryList.SIX_YEARS").type(JsonFieldType.STRING).description("6주년"),
                                      fieldWithPath("data.anniversaryList.SEVEN_YEARS").type(JsonFieldType.STRING).description("7주년"),
                                      fieldWithPath("data.anniversaryList.EIGHT_YEARS").type(JsonFieldType.STRING).description("8주년"),
                                      fieldWithPath("data.anniversaryList.NINE_YEARS").type(JsonFieldType.STRING).description("9주년"),
                                      fieldWithPath("data.anniversaryList.TEN_YEARS").type(JsonFieldType.STRING).description("10주년")
                              )));
    }

    @Test
    @DisplayName("해당 Member Id의 프로필을 조회한다.")
    void details() throws Exception {
        // given
        Long mockId = 1L;
        Profile mockProfile = getProfile(mockId, "홍길동", getLocalDate("2023-05-15"), Gender.MALE);

        given(profileService.findProfileByMemberId(anyLong()))
                .willReturn(mockProfile);

        // when
        ResultActions result = this.mockMvc.perform(
                get("/api/v1/{id}/profile", mockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"));

        // then
        result.andExpect(status().isOk())
              .andDo(print())
              .andDo(document("profile-details",
                              getDocumentRequest(),
                              getDocumentResponse(),
                              pathParameters(parameterWithName("id").description("member id")),
                              responseFields(
                                      fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                                      fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                      fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                      fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("Member ID"),
                                      fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("별칭"),
                                      fieldWithPath("data.gender").type(JsonFieldType.STRING).description("성별"),
                                      fieldWithPath("data.profileImageUrl").type(JsonFieldType.STRING).description("프로필 이미지 URL").optional(),
                                      fieldWithPath("data.linkedFlag").type(JsonFieldType.BOOLEAN).description("커플 연동 여부"),
                                      fieldWithPath("data.anniversaryList").type(JsonFieldType.OBJECT).description("기념일 정보"),
                                      fieldWithPath("data.anniversaryList.FIRST_DATE").type(JsonFieldType.STRING).description("사귄 날짜"),
                                      fieldWithPath("data.anniversaryList.ONE_HUNDRED").type(JsonFieldType.STRING).description("백일"),
                                      fieldWithPath("data.anniversaryList.TWO_HUNDREDS").type(JsonFieldType.STRING).description("이백일"),
                                      fieldWithPath("data.anniversaryList.THREE_HUNDREDS").type(JsonFieldType.STRING).description("삼백일"),
                                      fieldWithPath("data.anniversaryList.ONE_YEAR").type(JsonFieldType.STRING).description("1주년"),
                                      fieldWithPath("data.anniversaryList.TWO_YEARS").type(JsonFieldType.STRING).description("2주년"),
                                      fieldWithPath("data.anniversaryList.THREE_YEARS").type(JsonFieldType.STRING).description("3주년"),
                                      fieldWithPath("data.anniversaryList.FOUR_YEARS").type(JsonFieldType.STRING).description("4주년"),
                                      fieldWithPath("data.anniversaryList.FIVE_YEARS").type(JsonFieldType.STRING).description("5주년"),
                                      fieldWithPath("data.anniversaryList.SIX_YEARS").type(JsonFieldType.STRING).description("6주년"),
                                      fieldWithPath("data.anniversaryList.SEVEN_YEARS").type(JsonFieldType.STRING).description("7주년"),
                                      fieldWithPath("data.anniversaryList.EIGHT_YEARS").type(JsonFieldType.STRING).description("8주년"),
                                      fieldWithPath("data.anniversaryList.NINE_YEARS").type(JsonFieldType.STRING).description("9주년"),
                                      fieldWithPath("data.anniversaryList.TEN_YEARS").type(JsonFieldType.STRING).description("10주년")
                              )));
    }

    @Test
    @DisplayName("해당 Member ID의 Profile을 수정한다.")
    void modify() throws Exception {
        // given
        Long mockId = 1L;
        MockMultipartFile mockImage = getMultipartFile();
        Profile mockProfile = getProfile(mockId, "홍길동", getLocalDate("2023-05-15"), Gender.MALE);


        given(profileService.update(any()))
                .willReturn(mockProfile);

        // when
        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest("적길동", "2021-05-15", "FEMALE");
        String contents = objectMapper.writeValueAsString(profileUpdateRequest);

        ResultActions result = this.mockMvc.perform(
                multipart("/api/v1/{id}/profile", mockId)
                        .file(mockImage)
                        .file(new MockMultipartFile("profileUpdateRequest", "", "application/json",
                                                    contents.getBytes(StandardCharsets.UTF_8)))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }));

        // then
        result.andExpect(status().isOk())
              .andDo(print())
              .andDo(document("profile-modify",
                              getDocumentRequest(),
                              getDocumentResponse(),
                              pathParameters(
                                      parameterWithName("id").description("Member Id")
                              ),
                              requestPartBody("image"),
                              requestPartFields("profileUpdateRequest",
                                                fieldWithPath("nickname").type(JsonFieldType.STRING)
                                                                         .description("수정할 닉네임"),
                                                fieldWithPath("firstDate").type(JsonFieldType.STRING)
                                                                          .description("수정할 사귄 날짜"),
                                                fieldWithPath("gender").type(JsonFieldType.STRING)
                                                                       .description("수정할 성별")
                              ),
                              responseFields(
                                      fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                                      fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                      fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                      fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("Member ID"),
                                      fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("별칭"),
                                      fieldWithPath("data.gender").type(JsonFieldType.STRING).description("성별"),
                                      fieldWithPath("data.profileImageUrl").type(JsonFieldType.STRING).description("프로필 이미지 URL").optional(),
                                      fieldWithPath("data.linkedFlag").type(JsonFieldType.BOOLEAN).description("커플 연동 여부"),
                                      fieldWithPath("data.anniversaryList").type(JsonFieldType.OBJECT).description("기념일 정보"),
                                      fieldWithPath("data.anniversaryList.FIRST_DATE").type(JsonFieldType.STRING).description("사귄 날짜"),
                                      fieldWithPath("data.anniversaryList.ONE_HUNDRED").type(JsonFieldType.STRING).description("백일"),
                                      fieldWithPath("data.anniversaryList.TWO_HUNDREDS").type(JsonFieldType.STRING).description("이백일"),
                                      fieldWithPath("data.anniversaryList.THREE_HUNDREDS").type(JsonFieldType.STRING).description("삼백일"),
                                      fieldWithPath("data.anniversaryList.ONE_YEAR").type(JsonFieldType.STRING).description("1주년"),
                                      fieldWithPath("data.anniversaryList.TWO_YEARS").type(JsonFieldType.STRING).description("2주년"),
                                      fieldWithPath("data.anniversaryList.THREE_YEARS").type(JsonFieldType.STRING).description("3주년"),
                                      fieldWithPath("data.anniversaryList.FOUR_YEARS").type(JsonFieldType.STRING).description("4주년"),
                                      fieldWithPath("data.anniversaryList.FIVE_YEARS").type(JsonFieldType.STRING).description("5주년"),
                                      fieldWithPath("data.anniversaryList.SIX_YEARS").type(JsonFieldType.STRING).description("6주년"),
                                      fieldWithPath("data.anniversaryList.SEVEN_YEARS").type(JsonFieldType.STRING).description("7주년"),
                                      fieldWithPath("data.anniversaryList.EIGHT_YEARS").type(JsonFieldType.STRING).description("8주년"),
                                      fieldWithPath("data.anniversaryList.NINE_YEARS").type(JsonFieldType.STRING).description("9주년"),
                                      fieldWithPath("data.anniversaryList.TEN_YEARS").type(JsonFieldType.STRING).description("10주년")
                              )));
    }

    private Profile getProfile(Long memberId, String nickname, LocalDate firstDate, Gender gender) {
        return Profile.builder()
                      .memberId(memberId)
                      .nickname(nickname)
                      .firstDate(firstDate)
                      .gender(gender)
                      .build();
    }

    private MockMultipartFile getMultipartFile() {
        String path = "image.png";
        String contentType = "image/png";

        return new MockMultipartFile("image", path, contentType, "image".getBytes());
    }

    private LocalDate getLocalDate(String firstDate) {
        return LocalDate.parse(firstDate, DateTimeFormatter.ISO_DATE);
    }
}