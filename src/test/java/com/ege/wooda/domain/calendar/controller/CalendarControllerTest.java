package com.ege.wooda.domain.calendar.controller;

import com.ege.wooda.domain.calendar.domain.Calendar;
import com.ege.wooda.domain.calendar.dto.request.CalendarCreateRequest;
import com.ege.wooda.domain.calendar.dto.request.CalendarUpdateRequest;
import com.ege.wooda.domain.calendar.service.CalendarService;
import com.ege.wooda.domain.member.domain.Gender;
import com.ege.wooda.domain.member.domain.Member;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.ege.wooda.global.util.ApiDocumentUtils.getDocumentRequest;
import static com.ege.wooda.global.util.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(CalendarController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
public class CalendarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CalendarService calendarService;

    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("Calendar를 생성한다.")
    public void add() throws Exception{
        String mockNickname = "홍길동";
        Member mockMember = getMember(mockNickname, Gender.MALE, getLocalDate("2023-05-15"));

        CalendarCreateRequest calendarCreateRequest=CalendarCreateRequest.builder()
                .memberId(1L)
                .title("Calendar title")
                .memo("Calendar memo")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();

        String request=objectMapper.writeValueAsString(calendarCreateRequest);

        given(calendarService.save(any()))
                .willReturn(2L);
        given(memberService.findById(anyLong()))
                .willReturn(mockMember);

        ResultActions resultActions=this.mockMvc.perform(
                multipart("/api/v1/calendar")
                        .content(request)
                        .contentType("application/json")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        );

        resultActions.andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andDo(document("calendar-save",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data").type(JsonFieldType.NUMBER).description("응답 데이터")
                        )));
    }

    @Test
    @DisplayName("Member ID에 해당하는 일정을 조회한다.")
    public void getAll() throws Exception{
        List<Calendar> calendarList=getScheduleList();
        List<Long> mockIdList = new ArrayList<>(Arrays.asList(1L,2L));
        List<String> mockAuditList = new ArrayList<>(
                Arrays.asList("2023-07-15T17:13:06", "2023-07-20T09:34:21", "2023-07-31T23:11:42",
                        "2023-07-02T06:23:41"));

        ReflectionTestUtils.setField(calendarList.get(0), "id", mockIdList.get(0));
        ReflectionTestUtils.setField(calendarList.get(1), "id", mockIdList.get(1));

        ReflectionTestUtils.setField(calendarList.get(0).getAuditEntity(), "createdAt",
                getLocalDateTime(mockAuditList.get(0)));
        ReflectionTestUtils.setField(calendarList.get(0).getAuditEntity(), "updatedAt",
                getLocalDateTime(mockAuditList.get(0)));
        ReflectionTestUtils.setField(calendarList.get(1).getAuditEntity(), "createdAt",
                getLocalDateTime(mockAuditList.get(1)));
        ReflectionTestUtils.setField(calendarList.get(1).getAuditEntity(), "updatedAt",
                getLocalDateTime(mockAuditList.get(2)));

        given(calendarService.findByMemberId(2L))
                .willReturn(calendarList);

        ResultActions resultActions=this.mockMvc.perform(
                get("/api/v1/calendar?memberId=2")
                        .param("memberId", String.valueOf(2L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"));

        resultActions.andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andDo(document("calendar-getAll",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data").type(JsonFieldType.ARRAY).description("응답 데이터"),
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER)
                                        .description("Calendar ID").optional(),
                                fieldWithPath("data[].memberId").type(JsonFieldType.NUMBER)
                                        .description("Member ID").optional(),
                                fieldWithPath("data[].title").type(JsonFieldType.STRING)
                                        .description("제목").optional(),
                                fieldWithPath("data[].memo").type(JsonFieldType.STRING)
                                        .description("내용").optional(),
                                fieldWithPath("data[].startDate").type(JsonFieldType.STRING)
                                        .description("일정 시작시간").optional(),
                                fieldWithPath("data[].endDate").type(JsonFieldType.STRING)
                                        .description("일정 종료시간").optional(),
                                fieldWithPath("data[].auditEntity").type(JsonFieldType.OBJECT)
                                        .description("생성된 시간").optional(),
                                fieldWithPath("data[].auditEntity.createdAt").type(JsonFieldType.STRING)
                                        .description("생성된 시간").optional(),
                                fieldWithPath("data[].auditEntity.updatedAt").type(JsonFieldType.STRING)
                                        .description("수정된 시간").optional()
                        )
                        ));
    }

    @Test
    @DisplayName("특정한 일정을 조회한다.")
    public void findById() throws Exception{
        Calendar calendar = getSchedule(2L, "Test schedule1", "Test test 1", LocalDateTime.now(), LocalDateTime.now());

        ReflectionTestUtils.setField(calendar, "id", 6L);
        ReflectionTestUtils.setField(calendar.getAuditEntity(), "createdAt",
                getLocalDateTime("2023-07-01T19:02:14"));
        ReflectionTestUtils.setField(calendar.getAuditEntity(), "updatedAt",
                getLocalDateTime("2023-07-01T19:02:14"));


        given(calendarService.findById(anyLong()))
                .willReturn(calendar);

        ResultActions resultActions=this.mockMvc.perform(
                get("/api/v1/calendar/{id}", 6L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"));

        resultActions.andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andDo(document("calendar-findById",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(parameterWithName("id").description("일정 Id")),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                        .description("Calendar Id"),
                                fieldWithPath("data.memberId").type(JsonFieldType.NUMBER)
                                        .description("Member Id"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("data.memo").type(JsonFieldType.STRING)
                                        .description("내용"),
                                fieldWithPath("data.startDate").type(JsonFieldType.STRING)
                                        .description("일정 시작시간"),
                                fieldWithPath("data.endDate").type(JsonFieldType.STRING)
                                        .description("일정 종료시간"),
                                fieldWithPath("data.auditEntity.createdAt").type(JsonFieldType.STRING)
                                        .description("생성된 시간"),
                                fieldWithPath("data.auditEntity.updatedAt").type(JsonFieldType.STRING)
                                        .description("수정된 시간")
                        )));
    }

    @Test
    @DisplayName("해당 ID의 일정 정보를 수정한다.")
    public void modify() throws Exception{
        Calendar calendar = getSchedule(2L, "Test schedule1", "Test test 1", LocalDateTime.now(), LocalDateTime.now());

        ReflectionTestUtils.setField(calendar, "id", 6L);
        ReflectionTestUtils.setField(calendar.getAuditEntity(), "createdAt",
                getLocalDateTime("2023-07-01T19:02:14"));
        ReflectionTestUtils.setField(calendar.getAuditEntity(), "updatedAt",
                getLocalDateTime("2023-07-01T19:02:14"));

        given(calendarService.findById(anyLong()))
                .willReturn(calendar);
        given(calendarService.update(anyLong(),any()))
                .willReturn(6L);

        CalendarUpdateRequest calendarUpdateRequest=CalendarUpdateRequest.builder()
                .memberId(1L)
                .title("Update calendar title")
                .memo("Update memo")
                .build();

        String request=objectMapper.writeValueAsString(calendarUpdateRequest);

        ResultActions resultActions=this.mockMvc.perform(
                 multipart("/api/v1/calendar/{id}",6L)
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
                .andDo(document("calendar-update",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(parameterWithName("id").description("일정 Id")),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data").type(JsonFieldType.NUMBER).description("응답 데이터")
                        )));
    }

    @Test
    @DisplayName("특정 일정 정보를 삭제한다.")
    public void remove() throws Exception{
        Calendar calendar = getSchedule(2L, "Test schedule1", "Test test 1", LocalDateTime.now(), LocalDateTime.now());

        ReflectionTestUtils.setField(calendar, "id", 6L);
        ReflectionTestUtils.setField(calendar.getAuditEntity(), "createdAt",
                getLocalDateTime("2023-07-01T19:02:14"));
        ReflectionTestUtils.setField(calendar.getAuditEntity(), "updatedAt",
                getLocalDateTime("2023-07-01T19:02:14"));

        given(calendarService.findById(anyLong()))
                .willReturn(calendar);

        ResultActions resultActions=this.mockMvc.perform(
                delete("/api/v1/calendar/{id}", 6L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andDo(document("calendar-delete",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("Calendar Id")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data").description("응답 데이터")
                        )
                        ));
    }

    private Member getMember(String nickname, Gender gender, LocalDate firstDate) {
        return Member.builder()
                .uuid(UUID.randomUUID().toString())
                .nickname(nickname)
                .firstDate(firstDate)
                .gender(gender)
                .pictureM(
                        "https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&prefix=member/"
                                + nickname
                                + "/male.png")
                .pictureW(
                        "https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&prefix=member/"
                                + nickname
                                + "/female.png")
                .build();
    }

    private Calendar getSchedule(Long memberId,
                                 String title,
                                 String memo,
                                 LocalDateTime startDate,
                                 LocalDateTime endDate) {
        return Calendar.builder()
                .memberId(memberId)
                .title(title)
                .memo(memo)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    private List<Calendar> getScheduleList() {
        return List.of(
                getSchedule(2L, "Test schedule2", "Test test 2", LocalDateTime.now(), LocalDateTime.now()),
                getSchedule(2L, "Test schedule3", "Test test 3", LocalDateTime.now(), LocalDateTime.now())
        );
    }

    private LocalDate getLocalDate(String memoryDate) {
        return LocalDate.parse(memoryDate, DateTimeFormatter.ISO_DATE);
    }

    private LocalDateTime getLocalDateTime(String date) {
        return LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
