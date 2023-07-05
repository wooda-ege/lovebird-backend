package com.ege.wooda.domain.calendar.dto.request;

import com.ege.wooda.domain.calendar.domain.Calendar;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

public record CalendarUpdateRequest(@NotNull Long memberId,
                                    String title,
                                    String memo,
                                    @NotNull LocalDate startDate,
                                    LocalDate endDate)
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
