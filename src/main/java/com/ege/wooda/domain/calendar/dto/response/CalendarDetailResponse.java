package com.ege.wooda.domain.calendar.dto.response;

import lombok.Builder;

import java.time.LocalDate;

public record CalendarDetailResponse(Long memberId,
                                     String title,
                                     String memo,
                                     LocalDate startDate,
                                     LocalDate endDate) {

    @Builder
    public CalendarDetailResponse {}
}
