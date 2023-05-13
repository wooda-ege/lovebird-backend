//package com.ege.wooda.controller;
//
//import com.ege.wooda.domain.diary.repository.DiaryRepository;
//import com.ege.wooda.domain.diary.dto.DiaryCreateRequest;
//import org.junit.After;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.sql.Timestamp;
//import java.util.Date;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class DiaryControllerTest {
//    @LocalServerPort
//    private int port;
//
//    @Autowired
//    private DiaryRepository diaryRepository;
//
//    @After
//    public void clearAll() throws Exception{
//        diaryRepository.deleteAll();
//    }
//
//    @Test
//    public void postDiaryTest() throws Exception{
//        Date date=new Date();
//        String title="Title1";
//        String subTitle="Subtitle1";
//        Date memory_date=new Timestamp(date.getTime());
//        String place="Place1";
//        String contents="sample contents";
//        Date create_date=date;
//
//        DiaryCreateRequest diaryCreateRequest = DiaryCreateRequest.builder()
//                .title(title)
//                .subTitle(subTitle)
//                .memoryDate(memory_date)
//                .place(place)
//                .contents(contents)
//                .createDate(create_date)
//                .build();
//        String url="http://localhost:"+port+"/diaries";
//    }
//}
