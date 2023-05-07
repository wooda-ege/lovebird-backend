package com.ege.wooda.service;

import com.ege.wooda.domain.diary.DiaryRepository;
import com.ege.wooda.domain.diary.Diary;
import com.ege.wooda.dto.DiaryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepository;

    @Transactional
    public Long saveDiary(DiaryDTO diaryDTO){
        return diaryRepository.save(diaryDTO.toEntity()).getId();
    }

    @Transactional
    public List<Diary> findDiaries(){
        return (List<Diary>) diaryRepository.findAll();
    }

    @Transactional
    public Optional<Diary> findOne(Long diaryId){
        return diaryRepository.findById(diaryId);
    }
}
