package com.ege.wooda.domain.calendar.dto.request;

import com.ege.wooda.domain.calendar.domain.Calendar;
import lombok.Builder;

import java.time.LocalDateTime;

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
}
