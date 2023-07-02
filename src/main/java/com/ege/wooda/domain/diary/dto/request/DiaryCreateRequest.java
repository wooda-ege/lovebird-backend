package com.ege.wooda.domain.diary.dto.request;

import com.ege.wooda.domain.diary.domain.Diary;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record DiaryCreateRequest(String title,
                                 String subTitle,
                                 String memoryDate,
                                 String place,
                                 String content) {

    @Builder
    public DiaryCreateRequest {}

    public Diary toEntity(Long memberId) {
        return Diary.builder()
                    .memberId(memberId)
                    .title(title)
                    .subTitle(subTitle)
                    .memoryDate(getLocalDate(memoryDate))
                    .place(place)
                    .content(content)
                    .build();
    }

    private LocalDate getLocalDate(String memoryDate) {
        return LocalDate.parse(memoryDate, DateTimeFormatter.ISO_DATE);
    }

    private LocalDateTime getLocalDateTime(String date) {
        return LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
