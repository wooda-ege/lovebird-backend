package com.ege.wooda.domain.calendar.dto.request;

import com.ege.wooda.domain.calendar.domain.Calendar;
import com.ege.wooda.domain.calendar.domain.enums.Alarm;
import com.ege.wooda.domain.calendar.domain.enums.Color;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

public record CalendarUpdateRequest(@NotNull Long memberId,
                                    String title,
                                    String memo,
                                    Color color,
                                    Alarm alarm,
                                    @NotNull LocalDate startDate,
                                    LocalDate endDate,
                                    LocalTime startTime,
                                    LocalTime endTime)
{
    @Builder
    public CalendarUpdateRequest {}

    public Calendar toEntity(){
        return Calendar.builder()
                .memberId(memberId)
                .title(title)
                .memo(memo)
                .color(color)
                .alarm(alarm)
                .startDate(startDate)
                .endDate(endDate)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }
}
