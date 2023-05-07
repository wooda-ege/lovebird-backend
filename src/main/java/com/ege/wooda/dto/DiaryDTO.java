package com.ege.wooda.dto;

import com.ege.wooda.domain.diary.Diary;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
public class DiaryDTO {
    private String title;
    private String subTitle;
    private Date memory_date;
    private String place;
    private String contents;
    private Date create_date;
    private Date update_date;

    @Builder
    public DiaryDTO(String title, String subTitle, Date memoryDate, String place, String contents, Date createDate, Date updateDate) {
        this.title = title;
        this.subTitle = subTitle;
        this.memory_date = memoryDate;
        this.place = place;
        this.contents = contents;
        this.create_date = createDate;
        this.update_date = updateDate;
    }

    public Diary toEntity(){
        return Diary.builder()
                .title(title)
                .subTitle(subTitle)
                .memoryDate(memory_date)
                .place(place)
                .contents(contents)
                .createDate(create_date)
                .updateDate(update_date)
                .build();
    }
}
