package com.ege.wooda.domain.calendar.dto.request;

import com.ege.wooda.domain.calendar.domain.Calendar;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

public record CalendarCreateRequest(Long memberId, String title, String memo, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {

    @Builder
    public CalendarCreateRequest {}

    public Calendar toEntity(){
        return Calendar.builder()
                .memberId(memberId)
                .title(title)
                .memo(memo)
                .startDate(startDate)
                .endDate(endDate)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }
}
