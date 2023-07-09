package com.ege.wooda.domain.calendar.controller;

import com.ege.wooda.domain.calendar.domain.Calendar;
import com.ege.wooda.domain.calendar.domain.enums.Color;
import com.ege.wooda.domain.calendar.dto.request.CalendarCreateRequest;
import com.ege.wooda.domain.calendar.dto.request.CalendarUpdateRequest;
import com.ege.wooda.domain.calendar.service.CalendarService;
import com.ege.wooda.domain.member.service.MemberService;
import com.ege.wooda.global.security.jwt.util.JwtValidator;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ege.wooda.global.docs.ApiDocumentUtils.getDocumentRequest;
import static com.ege.wooda.global.docs.ApiDocumentUtils.getDocumentResponse;
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

    @MockBean
    private JwtValidator jwtValidator;

    @Test
    @DisplayName("Calendar를 생성한다.")
    public void add() throws Exception{
        CalendarCreateRequest calendarCreateRequest=CalendarCreateRequest.builder()
                .memberId(1L)
                .title("Calendar title")
                .memo("Calendar memo")
                .color(Color.PRIMARY.toString())
                .startDate(toLocalDate("2023-06-30"))
                .endDate(toLocalDate("2023-06-30"))
                .build();

        String request=objectMapper.writeValueAsString(calendarCreateRequest);

        given(calendarService.save(any()))
                .willReturn(2L);

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
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("Calendar ID").optional(),
                                fieldWithPath("data[].memberId").type(JsonFieldType.NUMBER)
                                        .description("Member ID").optional(),
                                fieldWithPath("data[].title").type(JsonFieldType.STRING)
                                        .description("제목").optional(),
                                fieldWithPath("data[].memo").type(JsonFieldType.STRING)
                                        .description("내용").optional(),
                                fieldWithPath("data[].color").type(JsonFieldType.STRING)
                                        .description("일정 색깔").optional(),
                                fieldWithPath("data[].startDate").type(JsonFieldType.STRING)
                                        .description("일정 시작일"),
                                fieldWithPath("data[].endDate").type(JsonFieldType.STRING)
                                        .description("일정 종료일").optional(),
                                fieldWithPath("data[].startTime").type(JsonFieldType.STRING)
                                        .description("일정 시작시간").optional(),
                                fieldWithPath("data[].endTime").type(JsonFieldType.STRING)
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
        Calendar calendar = getSchedule(2L, "Test schedule1", "Test test 1", Color.PRIMARY, toLocalDate("2023-06-28"), toLocalDate("2023-06-28"),toLocalTime("1:00"), toLocalTime("1:00"));

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
                                fieldWithPath("data.color").type(JsonFieldType.STRING)
                                        .description("일정 색깔"),
                                fieldWithPath("data.startDate").type(JsonFieldType.STRING)
                                        .description("일정 시작일"),
                                fieldWithPath("data.endDate").type(JsonFieldType.STRING)
                                        .description("일정 종료일").optional(),
                                fieldWithPath("data.startTime").type(JsonFieldType.STRING)
                                        .description("일정 시작시간").optional(),
                                fieldWithPath("data.endTime").type(JsonFieldType.STRING)
                                        .description("일정 종료시간").optional()
                        )));
    }

    @Test
    @DisplayName("해당 ID의 일정 정보를 수정한다.")
    public void modify() throws Exception{
        Calendar calendar = getSchedule(2L, "Test schedule1", "Test test 1", Color.PRIMARY, toLocalDate("2023-06-29"), toLocalDate("2023-06-29"), toLocalTime("14:00"), toLocalTime("15:00"));

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
                .color(Color.GRAY)
                .startDate(toLocalDate("2023-07-02"))
                .endDate(toLocalDate("2023-07-02"))
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
        Calendar calendar = getSchedule(2L, "Test schedule1", "Test test 1", Color.SECONDARY, LocalDate.now(), LocalDate.now(),toLocalTime("22:00"), toLocalTime("22:00"));

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

    private Calendar getSchedule(Long memberId,
                                 String title,
                                 String memo,
                                 Color color,
                                 LocalDate startDate,
                                 LocalDate endDate,
                                 LocalTime startTime,
                                 LocalTime endTime) {
        return Calendar.builder()
                .memberId(memberId)
                .title(title)
                .memo(memo)
                .color(Color.PRIMARY)
                .startDate(startDate)
                .endDate(endDate)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    private List<Calendar> getScheduleList() {
        return List.of(
                getSchedule(2L, "Test schedule2", "Test test 2", Color.SECONDARY, toLocalDate("2023-07-02"), toLocalDate("2023-07-02"),toLocalTime("8:00"), toLocalTime("9:00")),
                getSchedule(2L, "Test schedule3", "Test test 3", Color.GRAY, toLocalDate("2023-07-13"), toLocalDate("2023-07-13"),toLocalTime("17:00"), toLocalTime("22:00"))
        );
    }

    private LocalDateTime getLocalDateTime(String date) {
        return LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private LocalDate toLocalDate(String date){
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private LocalTime toLocalTime(String time){
        return LocalTime.parse(time, DateTimeFormatter.ofPattern("H:mm"));
    }
}
