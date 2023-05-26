package com.ege.wooda.domain.diary.service;

import com.ege.wooda.domain.diary.dto.request.DiaryCreateRequest;
import com.ege.wooda.domain.diary.repository.DiaryRepository;
import com.ege.wooda.domain.diary.Diary;
import com.ege.wooda.domain.diary.dto.request.DiaryUpdateRequest;
import com.ege.wooda.domain.member.domain.Member;
import com.ege.wooda.domain.member.repository.MemberRepository;
import com.ege.wooda.global.s3.ImageS3Uploader;
import com.ege.wooda.global.s3.S3File;
import com.ege.wooda.global.s3.dto.ImageDeleteRequest;
import com.ege.wooda.global.s3.dto.ImageUploadRequest;
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
    private final MemberRepository memberRepository;
    private final ImageS3Uploader imageS3Uploader;

    @Transactional
    public Long save(List<MultipartFile> imgs, DiaryCreateRequest diaryCreateRequest) throws IOException {
        Member member=findTargetMember(diaryCreateRequest.memberId());
        List<String> imgUrls=imageS3Uploader.upload(new ImageUploadRequest(imgs, "diary", member.getNickname())).stream()
                .map(S3File::fileUrl)
                .toList();

        Diary diary=diaryCreateRequest.toEntity(imgUrls);
        return diaryRepository.save(diary).getId();
    }

    @Transactional
    public Long update(Long id, List<MultipartFile> imgs, DiaryUpdateRequest updateDTO) throws IOException {
        Member member=findTargetMember(updateDTO.member_id());
        if(!imgs.isEmpty()){
            ImageDeleteRequest imgDeleteRequest=new ImageDeleteRequest(
                    imgs.stream().map(MultipartFile::getOriginalFilename).toList(),
                    "diary",
                    member.getNickname());
            imageS3Uploader.deleteFiles(imgDeleteRequest);
            imageS3Uploader.upload(new ImageUploadRequest(imgs,"diary",member.getNickname()));
        }
        Diary diary = diaryRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        diary.updateDiary(updateDTO.toEntity());
        return id;
    }

    @Transactional
    public void delete(Long id){
        Diary diary=diaryRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Member member=findTargetMember(diary.getMemberId());
        ImageDeleteRequest imgDeleteRequest=new ImageDeleteRequest(
                diary.getImgUrls(),
                "diary",
                member.getNickname());

        imageS3Uploader.deleteFiles(imgDeleteRequest);
        diaryRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Diary> findDiaries(){
        return diaryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Member findTargetMember(Long id){
        return memberRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Diary findOne(Long diaryId){
        return diaryRepository.findById(diaryId).orElseThrow(EntityNotFoundException::new);
    }
}
