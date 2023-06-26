package com.ege.wooda.domain.calendar.dto.request;

import com.ege.wooda.domain.calendar.domain.Calendar;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record CalendarCreateRequest(Long memberId, String title, String memo, LocalDateTime startDate, LocalDateTime endDate) {

    @Builder
    public CalendarCreateRequest {}

    public Calendar toEntity(){
        return Calendar.builder()
                .memberId(memberId)
                .title(title)
                .memo(memo)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    private LocalDate getLocalDate(String date){
        return LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
    }

    private LocalDateTime getLocalDateTime(String date){
        return LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
