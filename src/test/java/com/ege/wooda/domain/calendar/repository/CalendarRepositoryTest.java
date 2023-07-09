package com.ege.wooda.domain.calendar.repository;

import com.ege.wooda.domain.calendar.domain.Calendar;
import com.ege.wooda.domain.calendar.domain.enums.Color;
import com.ege.wooda.global.config.jpa.JpaConfig;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Import(JpaConfig.class)
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class CalendarRepositoryTest {
    @Autowired
    CalendarRepository calendarRepository;

    @AfterEach
    void cleanUp() { calendarRepository.deleteAll(); }

    @Test
    @DisplayName("새 일정을 생성한다.")
    public void save(){
        Calendar calendar=getSchedule(1L, "Test schedule1", "Test test 1", Color.SECONDARY, LocalDate.now(), LocalDate.now(), toLocalTime("13:00"), toLocalTime("15:00"));
        calendarRepository.save(calendar);

        Calendar existCalendar=calendarRepository.findById(calendar.getId()).orElseThrow(EntityNotFoundException::new);

        assertEquals(calendar.getId(), existCalendar.getId());
        assertEquals(calendar.getMemberId(), existCalendar.getMemberId());
        assertEquals(calendar.getTitle(),existCalendar.getTitle());
        assertEquals(calendar.getMemo(),existCalendar.getMemo());
        assertEquals(calendar.getColor(), existCalendar.getColor());
        assertEquals(calendar.getStartDate(), existCalendar.getStartDate());
        assertEquals(calendar.getEndDate(), existCalendar.getEndDate());
    }

    @Test
    @DisplayName("등록한 일정 조회에 성공한다.")
    public void findCalendarSuccess(){
        List<Calendar> calendarList=getScheduleList();
        calendarRepository.saveAll(calendarList);

        Calendar cal1=calendarList.get(0);
        Calendar existCalendar=calendarRepository.findById(cal1.getId()).orElseThrow(EntityNotFoundException::new);

        assertEquals(cal1.getId(), existCalendar.getId());
        assertEquals(cal1.getMemberId(), existCalendar.getMemberId());
        assertEquals(cal1.getTitle(), existCalendar.getTitle());
        assertEquals(cal1.getMemo(), existCalendar.getMemo());
        assertEquals(cal1.getColor(), existCalendar.getColor());
        assertEquals(cal1.getStartDate(), existCalendar.getStartDate());
        assertEquals(cal1.getEndDate(), existCalendar.getEndDate());

    }

    private Calendar getSchedule(Long memberId,
                                 String title,
                                 String memo,
                                 Color color,
                                 LocalDate startDate,
                                 LocalDate endDate,
                                 LocalTime startTime,
                                 LocalTime endTime){
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

    private List<Calendar> getScheduleList(){
        return List.of(
                        getSchedule(1L, "Test schedule1", "Test test 1", Color.PRIMARY, LocalDate.now(), LocalDate.now(),toLocalTime("9:00"),toLocalTime("11:00")),
                        getSchedule(2L, "Test schedule2", "Test test 2", Color.SECONDARY, LocalDate.now(), LocalDate.now(), toLocalTime("6:00"),toLocalTime("7:30")),
                        getSchedule(2L, "Test schedule3", "Test test 3", Color.GRAY, LocalDate.now(), LocalDate.now(),toLocalTime("19:00"),toLocalTime("22:00"))
        );
    }

    private LocalTime toLocalTime(String time){
        return LocalTime.parse(time, DateTimeFormatter.ofPattern("H:mm"));
    }
}
