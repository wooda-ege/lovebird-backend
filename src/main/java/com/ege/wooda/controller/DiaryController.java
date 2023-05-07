package com.ege.wooda.controller;

import com.ege.wooda.domain.diary.Diary;
import com.ege.wooda.dto.DiaryDTO;
import com.ege.wooda.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping("/diaries")
    public Long saveDiary(@RequestBody DiaryDTO diaryDTO){
        return diaryService.saveDiary(diaryDTO);
    }

    @GetMapping("/diaries")
    public List<Diary> getDiaryList(){
        List<Diary> resultList=diaryService.findDiaries();
        return resultList;
    }

    @GetMapping("/diaries/{id}")
    public Optional<Diary> getDiary(@PathVariable Long id){
        Optional<Diary> diary=diaryService.findOne(id);
        return diary;
    }
}
