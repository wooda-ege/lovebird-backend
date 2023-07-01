package com.ege.wooda.domain.calendar.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

public record CalendarDetailResponse(Long memberId,
                                     String title,
                                     String memo,
                                     LocalDateTime startDate,
                                     LocalDateTime endDate) {

    @Builder
    public CalendarDetailResponse {}
}
