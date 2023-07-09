package com.ege.wooda.domain.calendar.service;

import com.ege.wooda.domain.calendar.domain.Calendar;
import com.ege.wooda.domain.calendar.domain.enums.Color;
import com.ege.wooda.domain.calendar.dto.request.CalendarCreateRequest;
import com.ege.wooda.domain.calendar.dto.request.CalendarUpdateRequest;
import com.ege.wooda.domain.calendar.repository.CalendarRepository;
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

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

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

        Calendar mockCalendar = getSchedule(2L, "Test schedule1", "Test test 1", Color.SECONDARY, LocalDate.now(), LocalDate.now(),toLocalTime("9:00"), toLocalTime("10:30"));

        Long mockId=3L;

        CalendarCreateRequest calendarCreateRequest=CalendarCreateRequest.builder()
                .memberId(2L)
                .title("Calendar test")
                .memo("memo")
                .color(Color.SECONDARY.toString())
                .startDate(getLocalDate("2023-07-01"))
                .endDate(getLocalDate("2023-07-02"))
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
        Calendar calendar=getSchedule(2L, "Test schedule", "Test memo", Color.PRIMARY, LocalDate.now(), LocalDate.now(),toLocalTime("7:00"), toLocalTime("9:00"));

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
        Calendar calendar=getSchedule(2L, "Test schedule", "Test memo", Color.SECONDARY, LocalDate.now(), LocalDate.now(), toLocalTime("16:00"), toLocalTime("20:00"));

        Long mockId=3L;

        given(calendarRepository.findById(anyLong()))
                .willReturn(Optional.of(calendar));

        assertDoesNotThrow(()->calendarService.delete(mockId));
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
                .color(color)
                .startDate(startDate)
                .endDate(endDate)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    private List<Calendar> getScheduleList() {
        return List.of(
                getSchedule(2L, "Test schedule2", "Test test 2", Color.PRIMARY, LocalDate.now(), LocalDate.now(), toLocalTime("14:00"), toLocalTime("15:30")),
                getSchedule(2L, "Test schedule3", "Test test 3", Color.GRAY, LocalDate.now(), LocalDate.now(), toLocalTime("00:00"), toLocalTime("00:00"))
        );
    }

    private LocalDate getLocalDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private LocalTime toLocalTime(String time){
        return LocalTime.parse(time, DateTimeFormatter.ofPattern("H:mm"));
    }
}
