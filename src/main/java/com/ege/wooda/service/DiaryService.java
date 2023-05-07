package com.ege.wooda.service;

import com.ege.wooda.domain.diary.DiaryRepository;
import com.ege.wooda.domain.diary.Diary;
import com.ege.wooda.dto.Diary.DiaryDTO;
import com.ege.wooda.dto.Diary.DiaryUpdateDTO;
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
    public Long updateDiary(Long id, DiaryUpdateDTO updateDTO){
        Optional<Diary> diary = Optional.ofNullable(diaryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id = " + id)));
        diary.get().update(updateDTO.getTitle(), updateDTO.getSubTitle(), updateDTO.getMemory_date(), updateDTO.getPlace(), updateDTO.getContents(), updateDTO.getUpdate_date());
        return id;
    }

    @Transactional
    public List<Diary> findDiaries(){
        return (List<Diary>) diaryRepository.findAll();
    }

    @Transactional
    public Optional<Diary> findOne(Long diaryId){

        Optional<Diary> diary= Optional.ofNullable(diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id = " + diaryId)));
        return diary;
    }
}
