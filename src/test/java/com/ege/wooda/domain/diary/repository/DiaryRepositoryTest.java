package com.ege.wooda.domain.diary.repository;

import com.ege.wooda.domain.diary.Diary;
import com.ege.wooda.global.config.jpa.JpaConfig;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

@Import(JpaConfig.class)
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class DiaryRepositoryTest {
    @Autowired
    DiaryRepository diaryRepository;

    @AfterEach
    void cleanUp(){
        diaryRepository.deleteAll();
    }

    @Test
    @DisplayName("새 다이어리 글을 생성한다")
    public void save(){
        //Diary diary=get
    }

    private Diary getDiary(Long memberId, String title, String subTitle, LocalDate memoryDate, String place, String contents, List imgUrls){
        return Diary.builder()
                .memberId(memberId)
                .title(title)
                .subTitle(subTitle)
                .memoryDate(memoryDate)
                .place(place)
                .contents(contents)
                .imgUrls(imgUrls)
                .build();
    }

//    private List<Diary> getDiaryList(){
//        return List.of(getDiary(1, ))
//    }
}
