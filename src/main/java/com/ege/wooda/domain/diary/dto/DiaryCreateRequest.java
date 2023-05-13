package com.ege.wooda.domain.diary.dto;

import com.ege.wooda.domain.diary.Diary;
import lombok.Builder;

import java.time.LocalDate;

public record DiaryCreateRequest(String title, String sub_title, LocalDate memory_date, String place, String contents){

    @Builder
    public Diary toEntity(){
        return Diary.builder()
                .title(title)
                .subTitle(sub_title)
                .memoryDate(memory_date)
                .place(place)
                .contents(contents)
                .build();
    }
}
