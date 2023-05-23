package com.ege.wooda.domain.member.controller;

import com.ege.wooda.domain.member.domain.Gender;
import com.ege.wooda.domain.member.domain.Member;
import com.ege.wooda.domain.member.dto.request.MemberCreateRequest;
import com.ege.wooda.domain.member.dto.request.MemberUpdateRequest;
import com.ege.wooda.domain.member.service.MemberService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.ege.wooda.global.util.ApiDocumentUtils.getDocumentRequest;
import static com.ege.wooda.global.util.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class MemberControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("Member를 생성한다.")
    void add() throws Exception {
        // given
        List<MockMultipartFile> mockImages = getMultipartFiles();
        MemberCreateRequest memberCreateRequest = MemberCreateRequest.builder()
                .nickname("홍길동")
                .firstDate("2023-05-15")
                .gender("MALE")
                .build();
        String contents = objectMapper.writeValueAsString(memberCreateRequest);

        given(memberService.save(anyList(), any()))
                .willReturn(1L);

        // when
        ResultActions result = this.mockMvc.perform(
                multipart("/members")
                        .file(mockImages.get(0))
                        .file(mockImages.get(1))
                        .file(new MockMultipartFile("memberCreateRequest", "", "application/json", contents.getBytes(StandardCharsets.UTF_8)))
                        .contentType("multipart/form-data")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"));

        // then
        result.andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andDo(document("member-add",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestPartBody("images"),
                        requestPartFields("memberCreateRequest",
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("수정할 닉네임"),
                                fieldWithPath("firstDate").type(JsonFieldType.STRING).description("수정할 사귄 날짜"),
                                fieldWithPath("gender").type(JsonFieldType.STRING).description("수정할 성별")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("responseMessage").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data").type(JsonFieldType.NUMBER).description("Member ID")
                        )));
    }

    @Test
    @DisplayName("해당 Nickname의 Member를 조회한다.")
    void details() throws Exception {
        // given
        String mockNickname = "홍길동";
        Member mockMember = getMember(mockNickname, Gender.MALE, getLocalDate("2023-05-15"));

        given(memberService.findMemberByNickname(anyString()))
                .willReturn(mockMember);

        // when
        ResultActions result = this.mockMvc.perform(
                get("/members/{nickname}", mockNickname)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andDo(document("member-details",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(parameterWithName("nickname").description("닉네임")),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("responseMessage").type(JsonFieldType.STRING).description("응답 메시지"),

                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.uuid").type(JsonFieldType.STRING).description("UUID"),
                                fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("data.dDay").type(JsonFieldType.NUMBER).description("사귄 일수"),
                                fieldWithPath("data.gender").type(JsonFieldType.STRING).description("성별"),
                                fieldWithPath("data.pictureM").type(JsonFieldType.STRING).description("남자 사진"),
                                fieldWithPath("data.pictureW").type(JsonFieldType.STRING).description("여자 사진")
                        )));
    }

    @Test
    @DisplayName("해당 Nickname의 Member의 정보를 수정한다.")
    void modify() throws Exception {
        // given
        String mockNickname = "홍길동";
        List<MockMultipartFile> mockImages = getMultipartFiles();
        MemberUpdateRequest memberUpdateRequest = MemberUpdateRequest.builder()
                .nickname(mockNickname)
                .firstDate("2023-05-15")
                .gender("MALE")
                .build();
        String contents = objectMapper.writeValueAsString(memberUpdateRequest);

        given(memberService.update(anyString(), anyList(), any()))
                .willReturn(1L);

        // when
        ResultActions result = this.mockMvc.perform(
                multipart("/members/{nickname}", mockNickname)
                        .file(mockImages.get(0))
                        .file(mockImages.get(1))
                        .file(new MockMultipartFile("memberUpdateRequest", "", "application/json", contents.getBytes(StandardCharsets.UTF_8)))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }));

        // then
        result.andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andDo(document("member-modify",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("nickname").description("닉네임")
                        ),
                        requestPartBody("images"),
                        requestPartFields("memberUpdateRequest",
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("수정할 닉네임"),
                                fieldWithPath("firstDate").type(JsonFieldType.STRING).description("수정할 사귄 날짜"),
                                fieldWithPath("gender").type(JsonFieldType.STRING).description("수정할 성별")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("responseMessage").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data").type(JsonFieldType.NUMBER).description("Member ID")
                        )));
    }

    @Test
    @DisplayName("특정 Nickname의 Member를 삭제한다.")
    void remove() throws Exception {
        // given
        String mockNickname = "홍길동";

        // when
        ResultActions result = this.mockMvc.perform(
                delete("/members/{nickname}", mockNickname)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andDo(document("member-remove",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("nickname").description("닉네임")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("responseMessage").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data").description("NULL")
                        )));
    }

    private Member getMember(String nickname, Gender gender, LocalDate firstDate) {
        return Member.builder()
                .nickname(nickname)
                .firstDate(firstDate)
                .gender(gender)
                .pictureM("https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&prefix=member/"
                        + nickname
                        + "/male.png")
                .pictureW("https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&prefix=member/"
                        + nickname
                        + "/female.png")
                .build();
    }

    private List<MockMultipartFile> getMultipartFiles() {
        String path1 = "male.png";
        String contentType1 = "image/png";
        String path2 = "female.png";
        String contentType2 = "image/png";

        return List.of(new MockMultipartFile("images", path1, contentType1, "male".getBytes())
                , new MockMultipartFile("images", path2, contentType2, "female".getBytes()));
    }

    public static byte[] convert(List<MultipartFile> files) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (MultipartFile file : files) {
            baos.write(file.getBytes());
        }
        return baos.toByteArray();
    }

    private LocalDate getLocalDate(String firstDate) {
        return LocalDate.parse(firstDate, DateTimeFormatter.ISO_DATE);
    }
}