package com.ege.wooda.domain.diary.service;

import com.ege.wooda.domain.diary.dto.DiaryCreateRequest;
import com.ege.wooda.domain.diary.repository.DiaryRepository;
import com.ege.wooda.domain.diary.Diary;
import com.ege.wooda.domain.diary.dto.DiaryUpdateRequest;
import com.ege.wooda.global.s3.ImageS3Uploader;
import com.ege.wooda.global.s3.S3File;
import jakarta.persistence.EntityNotFoundException;
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
    public Long save(DiaryCreateRequest diary){
        return diaryRepository.save(diary.toEntity()).getId();
    }

    @Transactional
    public Long update(Long id, DiaryUpdateRequest updateDTO){
        Diary diary = diaryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다. id = " + id));
        diary.updateDiary(updateDTO.toEntity());
        return id;
    }

    @Transactional
    public void delete(Long id){
        diaryRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Diary> findDiaries(){
        return diaryRepository.findAll();
    }

    @Transactional(readOnly = true)
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
