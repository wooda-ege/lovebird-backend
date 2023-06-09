package com.ege.wooda.domain.diary.repository;

import com.ege.wooda.domain.diary.domain.Diary;
import com.ege.wooda.global.config.jpa.JpaConfig;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Import(JpaConfig.class)
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class DiaryRepositoryTest {
    @Autowired
    DiaryRepository diaryRepository;

    @AfterEach
    void cleanUp() {
        diaryRepository.deleteAll();
    }

    @Test
    @DisplayName("새 다이어리 글을 생성한다")
    public void save() {
        //Diary diary
        List<String> urls2 = getImageUrls("여혜민");
        Diary diary = getDiary(2L, "Test Diary2", "Test diary subtitle2", LocalDate.now(), "place2",
                               "contents2", urls2);
        diaryRepository.save(diary);

        Diary existDiary = diaryRepository.findById(diary.getId())
                                          .orElseThrow(EntityNotFoundException::new);

        assertEquals(diary.getId(), existDiary.getId());
        assertEquals(diary.getMemberId(), existDiary.getMemberId());
        assertEquals(diary.getTitle(), existDiary.getTitle());
        assertEquals(diary.getSubTitle(), existDiary.getSubTitle());
        assertEquals(diary.getMemoryDate(), existDiary.getMemoryDate());
        assertEquals(diary.getPlace(), existDiary.getPlace());
        assertEquals(diary.getContent(), existDiary.getContent());
        assertEquals(diary.getImgUrls(), existDiary.getImgUrls());
    }

    @Test
    @DisplayName("유저가 작성한 다이어리 목록 조회에 성공한다.")
    public void findDiaryByMemberSuccess() {
        List<Diary> diaryList = getDiaryList();

        diaryRepository.saveAll(diaryList);

        Diary diary1 = diaryList.get(0);
        Diary existDiary = diaryRepository.findById(diary1.getId()).orElseThrow(EntityNotFoundException::new);

        assertEquals(diary1.getId(), existDiary.getId());
        assertEquals(diary1.getMemberId(), existDiary.getMemberId());
        assertEquals(diary1.getTitle(), existDiary.getTitle());
        assertEquals(diary1.getImgUrls(), existDiary.getImgUrls());
    }

    @Test
    @DisplayName("유저가 작성한 다이어리 목록 조회에 실패한다.")
    public void findDiaryByMemberFail() {
        List<Diary> diaryList = getDiaryList();

        diaryRepository.saveAll(diaryList);
        assertThrows(EntityNotFoundException.class,
                     () -> diaryRepository.findById(10L).orElseThrow(EntityNotFoundException::new));
    }

    private Diary getDiary(Long memberId, String title, String subTitle, LocalDate memoryDate, String place,
                           String content, List<String> imgUrls) {
        return Diary.builder()
                    .memberId(memberId)
                    .title(title)
                    .subTitle(subTitle)
                    .memoryDate(memoryDate)
                    .place(place)
                    .content(content)
                    .imgUrls(imgUrls)
                    .build();
    }

    private List<Diary> getDiaryList() {
        List<String> urls1 = getImageUrls("홍길동");
        List<String> urls2 = getImageUrls("여혜민");
        return List.of(
                getDiary(1L, "Test Diary1", "Test diary subtitle1", LocalDate.now(), "place1", "contents1",
                         urls1)
                , getDiary(2L, "Test Diary2", "Test diary subtitle2", LocalDate.now(), "place2", "contents2",
                           urls2)
                , getDiary(2L, "Test Diary3", "Test diary subtitle3", LocalDate.now(), "place3", "contents3",
                           urls2));
    }

    private List<String> getImageUrls(String name) {
        String url1 =
                "https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&amp;prefix=member/"
                + name + "/1-1.png";
        String url2 =
                "https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&amp;prefix=member/"
                + name + "/1-2.png";

        return List.of(url1, url2);
    }
}
