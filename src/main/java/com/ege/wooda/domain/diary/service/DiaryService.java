package com.ege.wooda.domain.diary.service;

import com.ege.wooda.domain.diary.dto.request.DiaryCreateRequest;
import com.ege.wooda.domain.diary.dto.request.DiaryUpdateRequest;
import com.ege.wooda.domain.diary.repository.DiaryRepository;
import com.ege.wooda.domain.diary.domain.Diary;
import com.ege.wooda.global.s3.ImageS3Uploader;
import com.ege.wooda.global.common.enums.DomainName;
import com.ege.wooda.global.s3.dto.ImageDeleteRequest;
import com.ege.wooda.global.s3.dto.ImageUploadRequest;
import com.ege.wooda.global.s3.dto.S3File;
import com.ege.wooda.global.s3.fomatter.FileNameFormatter;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepository;
    private final ImageS3Uploader imageS3Uploader;
    private final FileNameFormatter fileNameFormatter;

    @Transactional
    public Long save(List<MultipartFile> images, DiaryCreateRequest diaryCreateRequest, Long memberId)
            throws IOException {
        Diary diary = diaryRepository.save(diaryCreateRequest.toEntity(memberId));

        if (images != null) {
            ImageUploadRequest imageUploadRequest = getImageUploadRequest(images, memberId, diary.getId());
            diary.saveImages(imageS3Uploader.upload(imageUploadRequest).stream()
                                            .map(S3File::fileUrl)
                                            .toList());
        }

        return diary.getId();
    }

    @Transactional
    public Long update(Long id, List<MultipartFile> images, DiaryUpdateRequest diaryUpdateRequest,
                       Long memberId) throws IOException {
        Diary diary = findById(id);
        List<String> imgUrls = diary.getImgUrls();

        if (images != null) {
            imageS3Uploader.deleteFiles(getImageDeleteRequest(imgUrls, memberId));

            ImageUploadRequest imageUploadRequest = getImageUploadRequest(images, memberId, diary.getId());
            imgUrls = imageS3Uploader.upload(imageUploadRequest).stream()
                                     .map(S3File::fileUrl)
                                     .toList();
        }

        diary.updateDiary(diaryUpdateRequest.toEntity(memberId, imgUrls));
        return id;
    }

    @Transactional
    public void delete(Long memberId, Long diaryId) {
        Diary diary = findByMemberIdAndDiaryId(memberId, diaryId);

        ImageDeleteRequest imgDeleteRequest = getImageDeleteRequest(
                diary.getImgUrls().stream()
                     .map(s -> s.substring(s.lastIndexOf("/") + 1))
                     .toList(),
                memberId
        );

        imageS3Uploader.deleteFiles(imgDeleteRequest);
        diaryRepository.delete(diary);
    }

    @Transactional(readOnly = true)
    public List<Diary> findDiaries() {
        return diaryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Diary findById(Long id) {
        return diaryRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Diary findByMemberIdAndDiaryId(Long memberId, Long diaryId) {
        return diaryRepository.findDiariesByMemberIdAndId(memberId, diaryId).orElseThrow(
                EntityNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<Diary> findByMemberId(Long id) {
        return diaryRepository.findDiariesByMemberId(id);
    }

    private ImageUploadRequest getImageUploadRequest(List<MultipartFile> images, Long memberId, Long diaryId) {
        return ImageUploadRequest.builder()
                                 .images(images)
                                 .imageNames(fileNameFormatter.generateImageNames(images, memberId, diaryId))
                                 .domain(DomainName.DIARY.getDomain())
                                 .memberId(memberId)
                                 .build();
    }

    private ImageDeleteRequest getImageDeleteRequest(List<String> imgUrls, Long memberId) {
        return new ImageDeleteRequest(
                imgUrls.stream()
                       .map(s -> s.substring(s.lastIndexOf("/") + 1))
                       .toList(),
                DomainName.DIARY.getDomain(),
                memberId);
    }
}
