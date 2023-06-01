package com.ege.wooda.service;

import com.ege.wooda.domain.diary.repository.DiaryRepository;
import com.ege.wooda.domain.diary.domain.Diary;
import com.ege.wooda.global.config.jpa.JpaConfig;
import jakarta.persistence.EntityNotFoundException;
import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@Import(JpaConfig.class)
@RunWith(SpringRunner.class)
@DataJpaTest
public class DiaryRepositoryTest {
    @Autowired
    DiaryRepository diaryRepository;

    @After
    public void cleanup() {
        diaryRepository.deleteAll();
    }

    @Test
    @DisplayName("새로운 게시글 생성한다.")
    public void save() {

        Diary diary=getDiary("테스트1", getLocalDate("2023-05-11"));
        diaryRepository.save(diary);

        Diary testDiary=diaryRepository.findById(diary.getId())
                        .orElseThrow(EntityNotFoundException::new);

        assertEquals(diary.getId(),testDiary.getId());
        assertEquals(diary.getTitle(), testDiary.getTitle());
        assertEquals(diary.getMemoryDate(), testDiary.getMemoryDate());
    }

    @Test
    @DisplayName("등록된 게시글 목록을 조회한다.")
    public void getList() {
        List<Diary> diaryList=getDiaryList();
        diaryRepository.saveAll(diaryList);

        Diary secondDiary=diaryList.get(1);
        Optional<Diary> testDiary=diaryRepository.findById(secondDiary.getId());

        assertEquals(secondDiary.getId(),testDiary.get().getId());
        assertEquals(secondDiary.getTitle(),testDiary.get().getTitle());
        assertEquals(secondDiary.getMemoryDate(),testDiary.get().getMemoryDate());
    }

    private Diary getDiary(String title, LocalDate memoryDate){
        return Diary.builder()
                .title(title)
                .memoryDate(memoryDate)
                .build();
    }

    private List<Diary> getDiaryList(){
        return List.of(getDiary("테스트1", getLocalDate("2023-05-11")),
                getDiary("테스트2",getLocalDate("2023-05-09")),
                getDiary("테스트3",getLocalDate("2023-04-10")));

    }

    private LocalDate getLocalDate(String date){
        return LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
    }
}
