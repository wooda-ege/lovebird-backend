package com.ege.wooda.domain.diary.dto.request;

import com.ege.wooda.domain.diary.Diary;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

public record DiaryUpdateRequest(@NotBlank Long member_id,
                                 @NotBlank String title,
                                 @NotBlank String sub_title,
                                 @NotBlank LocalDate memory_date,
                                 @NotBlank String place,
                                 @NotBlank String contents,
                                 @NotBlank List imgUrls) {

    @Builder
    public DiaryUpdateRequest {}

    public Diary toEntity(){
        return Diary.builder()
                .memberId(member_id)
                .title(title)
                .subTitle(sub_title)
                .memoryDate(memory_date)
                .place(place)
                .contents(contents)
                .imgUrls(imgUrls)
                .build();
    }
}
