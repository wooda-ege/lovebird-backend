package com.ege.wooda.domain.calendar.dto.request;

import com.ege.wooda.domain.calendar.domain.Calendar;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;

public record CalendarUpdateRequest(Long memberId,
                                    @NotBlank String title,
                                    @NotBlank String memo,
                                    LocalDateTime startDate,
                                    LocalDateTime endDate)
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
