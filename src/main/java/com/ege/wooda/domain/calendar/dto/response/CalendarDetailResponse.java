package com.ege.wooda.domain.calendar.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

public record CalendarDetailResponse(Long id,
                                     Long memberId,
                                     String title,
                                     String memo,
                                     String color,
                                     String alarm,
                                     LocalDate startDate,
                                     LocalDate endDate,
                                     LocalTime startTime,
                                     LocalTime endTime) {

    @Builder
    public CalendarDetailResponse {}
}
