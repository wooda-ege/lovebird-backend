package com.ege.wooda.service;

import com.ege.wooda.domain.diary.repository.DiaryRepository;
import com.ege.wooda.domain.diary.dao.Diary;
import com.ege.wooda.domain.diary.service.DiaryService;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class DiaryServiceTest {
    @Autowired
    DiaryService diaryService;
    @Autowired
    DiaryRepository diaryRepository;

    @After
    public void cleanup() {
        diaryRepository.deleteAll();
    }

    @Test
    public void diarySave() throws Exception{
        Date date=new Date();
        diaryRepository.save(Diary.builder()
                        .title("Test1")
                        .place("place1")
                        .contents("오늘은 ~~~를 갔다. 날씨가 너무 좋았고 ~~~했다. 다음에도 또 가야겠다. ^_^")
                        .memoryDate(new Timestamp(date.getTime()))
                        .createDate(date)
                        .build());

        List<Diary> diaryList= (List<Diary>) diaryRepository.findAll();

        assertEquals("Test1", diaryList.get(0).getTitle());
    }

    @Test
    public void diaryList() throws Exception{
        List<Diary> diaryList=diaryService.findDiaries();
        System.out.println(">> diary list : "+diaryList);
    }
}
