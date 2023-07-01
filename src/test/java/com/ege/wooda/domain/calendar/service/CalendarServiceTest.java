package com.ege.wooda.domain.calendar.service;

import com.ege.wooda.domain.calendar.domain.Calendar;
import com.ege.wooda.domain.calendar.dto.request.CalendarCreateRequest;
import com.ege.wooda.domain.calendar.dto.request.CalendarUpdateRequest;
import com.ege.wooda.domain.calendar.repository.CalendarRepository;
import com.ege.wooda.domain.member.domain.Gender;
import com.ege.wooda.domain.member.domain.Member;
import com.ege.wooda.global.config.jpa.JpaConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.sql.Ref;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@Import(JpaConfig.class)
@ExtendWith(MockitoExtension.class)
public class CalendarServiceTest {

    @InjectMocks
    private CalendarService calendarService;

    @Mock
    private CalendarRepository calendarRepository;

    @AfterEach
    public void cleanUp() {
        calendarRepository.deleteAll();
    }

    @Test
    @DisplayName("Calendar를 생성하면 해당 Calendar의 ID를 반환한다.")
    public void save() throws Exception {

        Member mockMember = getMember("홍길동", Gender.MALE, getLocalDate("2023-05-09"));
        Calendar mockCalendar = getSchedule(2L, "Test schedule1", "Test test 1", LocalDateTime.now(), LocalDateTime.now());

        Long mockId=3L;

        CalendarCreateRequest calendarCreateRequest=CalendarCreateRequest.builder()
                .memberId(2L)
                .title("Calendar test")
                .memo("memo")
                .startDate(getLocalDateTime("2023-07-01 17:00"))
                .endDate(getLocalDateTime("2023-07-02 20:00"))
                .build();

        ReflectionTestUtils.setField(mockCalendar, "id", mockId);
        given(calendarRepository.save(any()))
                .willReturn(mockCalendar);

        Long saveCalendarId=calendarService.save(calendarCreateRequest);
        assertEquals(saveCalendarId, mockId);

    }

    @Test
    @DisplayName("모든 calendar를 반환한다.")
    public void findAll(){
        List<Calendar> calendarList=getScheduleList();
        List<Long> mockIdList = new ArrayList<>(Arrays.asList(1L,2L));
        ReflectionTestUtils.setField(calendarList.get(0), "id", mockIdList.get(0));
        ReflectionTestUtils.setField(calendarList.get(1), "id", mockIdList.get(1));

        given(calendarService.findByMemberId(2L))
                .willReturn(calendarList);

        List<Calendar> calendars=calendarService.findByMemberId(2L);

        assertEquals(calendars, calendarList);
    }

    @Test
    @DisplayName("Calendar 정보를 수정하면 해당 calendar의 ID가 반환된다.")
    public void update() throws IOException{
        Calendar calendar=getSchedule(2L, "Test schedule", "Test memo", LocalDateTime.now(), LocalDateTime.now());

        Long mockId=3L;
        String updateTitle="Update calendar title";

        CalendarUpdateRequest calendarUpdateRequest=CalendarUpdateRequest.builder()
                .memberId(1L)
                .title(updateTitle)
                .build();

        ReflectionTestUtils.setField(calendar, "id", mockId);

        given(calendarRepository.findById(anyLong()))
                .willReturn(Optional.of(calendar));

        Long updateId=calendarService.update(mockId, calendarUpdateRequest);

        Long findId=calendarService.findById(mockId).getId();
        assertEquals(updateId, findId);

    }

    @Test
    @DisplayName("Calendar를 삭제하면 해당 ID가 반환된다.")
    public void delete(){
        Calendar calendar=getSchedule(2L, "Test schedule", "Test memo", LocalDateTime.now(), LocalDateTime.now());

        Long mockId=3L;

        given(calendarRepository.findById(anyLong()))
                .willReturn(Optional.of(calendar));

        assertDoesNotThrow(()->calendarService.delete(mockId));
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

    private Member getMember(String nickname, Gender gender, LocalDate firstDate) {
        return Member.builder()
                .nickname(nickname)
                .firstDate(firstDate)
                .gender(gender)
                .pictureM(null)
                .pictureW(null)
                .build();
    }

    private LocalDate getLocalDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
    }

    private LocalDateTime getLocalDateTime(String date) {
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    private String getUUID() {
        return UUID.randomUUID().toString();
    }
}
