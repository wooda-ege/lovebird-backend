package com.ege.wooda.domain.calendar.repository;

import com.ege.wooda.domain.calendar.domain.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {

    @Query("select c from Calendar c where memberId=?1")
    List<Calendar> findByMemberId(Long id);

}
