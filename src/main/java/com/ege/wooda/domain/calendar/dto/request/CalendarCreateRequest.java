package com.ege.wooda.domain.calendar.dto.request;

import com.ege.wooda.domain.calendar.domain.Calendar;
import com.ege.wooda.domain.calendar.domain.enums.Color;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

public record CalendarCreateRequest(@NotNull Long memberId,
                                    @NotBlank String title,
                                    String memo,
                                    @NotBlank String color,
                                    @NotNull LocalDate startDate,
                                    LocalDate endDate,
                                    LocalTime startTime,
                                    LocalTime endTime) {

    @Builder
    public CalendarCreateRequest {}

    public Calendar toEntity(){
        return Calendar.builder()
                .memberId(memberId)
                .title(title)
                .memo(memo)
                .color(Color.valueOf(color.toString()))
                .startDate(startDate)
                .endDate(endDate)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }
}
