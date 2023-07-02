package com.ege.wooda.domain.calendar.dto.request;

import com.ege.wooda.domain.calendar.domain.Calendar;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

public record CalendarUpdateRequest(@NotNull Long memberId,
                                    String title,
                                    String memo,
                                    @NotNull LocalDateTime startDate,
                                    @NotNull LocalDateTime endDate)
{
    @Builder
    public CalendarUpdateRequest {}

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
