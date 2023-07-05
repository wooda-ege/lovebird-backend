package com.ege.wooda.domain.calendar.repository;

import com.ege.wooda.domain.calendar.domain.Calendar;
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
        Calendar calendar=getSchedule(1L, "Test schedule1", "Test test 1", LocalDate.now(), LocalDate.now());
        calendarRepository.save(calendar);

        Calendar existCalendar=calendarRepository.findById(calendar.getId()).orElseThrow(EntityNotFoundException::new);

        assertEquals(calendar.getId(), existCalendar.getId());
        assertEquals(calendar.getMemberId(), existCalendar.getMemberId());
        assertEquals(calendar.getTitle(),existCalendar.getTitle());
        assertEquals(calendar.getMemo(),existCalendar.getMemo());
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
        assertEquals(cal1.getStartDate(), existCalendar.getStartDate());
        assertEquals(cal1.getEndDate(), existCalendar.getEndDate());

    }

    private Calendar getSchedule(Long memberId,
                                 String title,
                                 String memo,
                                 LocalDate startDate,
                                 LocalDate endDate){
        return Calendar.builder()
                .memberId(memberId)
                .title(title)
                .memo(memo)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    private List<Calendar> getScheduleList(){
        return List.of(
                        getSchedule(1L, "Test schedule1", "Test test 1", LocalDate.now(), LocalDate.now()),
                        getSchedule(2L, "Test schedule2", "Test test 2", LocalDate.now(), LocalDate.now()),
                        getSchedule(2L, "Test schedule3", "Test test 3", LocalDate.now(), LocalDate.now())
        );

    }
}
