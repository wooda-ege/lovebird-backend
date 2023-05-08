package com.ege.wooda.service;

import com.ege.wooda.domain.diary.DiaryRepository;
import com.ege.wooda.domain.diary.Diary;
import com.ege.wooda.dto.Diary.DiaryDTO;
import com.ege.wooda.dto.Diary.DiaryUpdateDTO;
import com.ege.wooda.global.s3.ImageS3Uploader;
import com.ege.wooda.global.s3.S3File;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepository;
    private final ImageS3Uploader imageS3Uploader;

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
    @Transactional
    public List<String> saveImage(List<MultipartFile> img) throws IOException {
        List<String> url = new ArrayList<String>();
        try{
            List<S3File> s3Files = imageS3Uploader.upload(img);
            for(S3File s3File : s3Files){
                url.add(s3File.fileUrl());
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new FileNotFoundException("이미지 저장 실패");
        }
        return url;
    }
}
