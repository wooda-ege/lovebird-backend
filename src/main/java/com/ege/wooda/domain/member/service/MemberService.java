package com.ege.wooda.domain.member.service;

import com.ege.wooda.domain.member.domain.Member;
import com.ege.wooda.domain.member.dto.request.MemberCreateRequest;
import com.ege.wooda.domain.member.dto.request.MemberUpdateRequest;
import com.ege.wooda.domain.member.repository.MemberRepository;
import com.ege.wooda.global.s3.ImageS3Uploader;
import com.ege.wooda.global.s3.S3File;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final ImageS3Uploader s3Uploader;

    @Transactional
    public Long save(List<MultipartFile> images, MemberCreateRequest memberCreateRequest) throws IOException {
        return memberRepository.save(memberCreateRequest.toEntity(s3Uploader.upload(images).stream()
                .map(S3File::fileUrl)
                .toList())).getId();
    }

    @Transactional
    public Long update(String nickname, List<MultipartFile> images, MemberUpdateRequest memberUpdateRequest) throws IOException {
        if(!images.isEmpty()) {
            s3Uploader.deleteFiles(images.stream().map(MultipartFile::getOriginalFilename).toList());
            s3Uploader.upload(images);
        }
        Member member = findMemberByNickname(nickname);
        member.update(memberUpdateRequest.toEntity());

        return member.getId();
    }

    @Transactional
    @CacheEvict(cacheNames = "member")
    public void delete(String nickname) {
        // 이미지 디렉토리 구조에 대해 논의 필요
        s3Uploader.deleteFiles(List.of(nickname + "/male.png", nickname + "female.png"));
        memberRepository.delete(findMemberByNickname(nickname));
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "member")
    public Member findMemberByNickname(String nickname) {
        return memberRepository.findMemberByNickname(nickname).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Member findById(Long id) {
        return memberRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
