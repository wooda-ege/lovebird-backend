package com.ege.wooda.domain.diary.dto.request;

import com.ege.wooda.domain.diary.domain.Diary;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record DiaryCreateRequest(Long memberId,String title, String subTitle, String memoryDate, String place, String contents, List<String> imgUrls){

    @Builder
    public DiaryCreateRequest {}

    public Diary toEntity(){
        return Diary.builder()
                .memberId(memberId)
                .title(title)
                .subTitle(subTitle)
                .memoryDate(getLocalDate(memoryDate))
                .place(place)
                .contents(contents)
                .build();
    }

    private LocalDate getLocalDate(String memoryDate) {
        return LocalDate.parse(memoryDate, DateTimeFormatter.ISO_DATE);
    }
    private LocalDateTime getLocalDateTime(String date) {
        return LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
