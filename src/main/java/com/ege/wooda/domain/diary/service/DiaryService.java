package com.ege.wooda.domain.diary.service;

import com.ege.wooda.domain.diary.dto.request.DiaryCreateRequest;
import com.ege.wooda.domain.diary.repository.DiaryRepository;
import com.ege.wooda.domain.diary.domain.Diary;
import com.ege.wooda.domain.diary.dto.request.DiaryUpdateRequest;
import com.ege.wooda.domain.member.service.MemberService;
import com.ege.wooda.global.s3.ImageS3Uploader;
import com.ege.wooda.global.s3.S3File;
import com.ege.wooda.global.s3.dto.DomainName;
import com.ege.wooda.global.s3.dto.ImageDeleteRequest;
import com.ege.wooda.global.s3.dto.ImageUploadRequest;
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
    public Long save(List<MultipartFile> images, DiaryCreateRequest diaryCreateRequest, String memberUUID)
            throws IOException {
        Diary diary = diaryRepository.save(diaryCreateRequest.toEntity());

        if (!images.isEmpty()) {
            ImageUploadRequest imageUploadRequest = getImageUploadRequest(images, memberUUID, diary.getId());
            diary.saveImages(imageS3Uploader.upload(imageUploadRequest).stream()
                                            .map(S3File::fileUrl)
                                            .toList());
        }

        return diary.getId();
    }

    @Transactional
    public Long update(Long id, List<MultipartFile> images, DiaryUpdateRequest diaryUpdateRequest,
                       String memberUUID) throws IOException {
        Diary diary = findById(id);
        List<String> imgUrls = diary.getImgUrls();

        if (!images.isEmpty()) {
            imageS3Uploader.deleteFiles(getImageDeleteRequest(imgUrls, memberUUID));

            ImageUploadRequest imageUploadRequest = getImageUploadRequest(images, memberUUID, diary.getId());
            imgUrls = imageS3Uploader.upload(imageUploadRequest).stream()
                                     .map(S3File::fileUrl)
                                     .toList();
        }

        diary.updateDiary(diaryUpdateRequest.toEntity(imgUrls));
        return id;
    }

    @Transactional
    public void delete(Long id, String memberUUID) {
        Diary diary = findById(id);

        ImageDeleteRequest imgDeleteRequest = getImageDeleteRequest(
                diary.getImgUrls().stream()
                     .map(s -> s.substring(s.lastIndexOf("/") + 1))
                     .toList(),
                memberUUID
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

    private ImageUploadRequest getImageUploadRequest(List<MultipartFile> images, String uuid, Long diaryId) {
        return ImageUploadRequest.builder()
                                 .images(images)
                                 .imageNames(fileNameFormatter.generateImageNames(images, uuid, diaryId))
                                 .domain(DomainName.DIARY.getDomain())
                                 .uuid(uuid)
                                 .build();
    }

    private ImageDeleteRequest getImageDeleteRequest(List<String> imgUrls, String uuid) {
        return new ImageDeleteRequest(
                imgUrls.stream()
                       .map(s -> s.substring(s.lastIndexOf("/") + 1))
                       .toList(),
                DomainName.DIARY.getDomain(),
                uuid
        );
    }
}
