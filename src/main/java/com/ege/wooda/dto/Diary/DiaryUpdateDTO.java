package com.ege.wooda.dto.Diary;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
public class DiaryUpdateDTO {
    private String title;
    private String subTitle;
    private Date memory_date;
    private String place;
    private String contents;
    private Date update_date;

    @Builder
    public DiaryUpdateDTO(String title, String subTitle, Date memoryDate, String place, String contents, Date update_date){
        this.title=title;
        this.subTitle=subTitle;
        this.memory_date=memoryDate;
        this.place=place;
        this.contents=contents;
        this.update_date=update_date;
    }
}
