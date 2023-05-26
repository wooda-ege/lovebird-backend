package com.ege.wooda.domain.member.service;

import com.ege.wooda.domain.member.domain.Member;
import com.ege.wooda.domain.member.dto.request.MemberCreateRequest;
import com.ege.wooda.domain.member.dto.request.MemberUpdateRequest;
import com.ege.wooda.domain.member.repository.MemberRepository;
import com.ege.wooda.global.s3.ImageS3Uploader;
import com.ege.wooda.global.s3.S3File;
import com.ege.wooda.global.s3.dto.ImageDeleteRequest;
import com.ege.wooda.global.s3.dto.ImageUploadRequest;
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
        List<String> imageUrls = s3Uploader.upload(new ImageUploadRequest(images, "profile", memberCreateRequest.nickname())).stream()
                .map(S3File::fileUrl)
                .toList();
        Member member = memberCreateRequest.toEntity(imageUrls);

        return memberRepository.save(member).getId();
    }

    @Transactional
    public Long update(String nickname, List<MultipartFile> images, MemberUpdateRequest memberUpdateRequest) throws IOException {
        if(!images.isEmpty()) {
            ImageDeleteRequest imageDeleteRequest = new ImageDeleteRequest(
                    images.stream().map(MultipartFile::getOriginalFilename).toList(),
                    "profile",
                    memberUpdateRequest.nickname());

            s3Uploader.deleteFiles(imageDeleteRequest);
            s3Uploader.upload(new ImageUploadRequest(images, "profile", memberUpdateRequest.nickname()));
        }
        Member member = findMemberByNickname(nickname);
        member.update(memberUpdateRequest.toEntity());

        return member.getId();
    }

    @Transactional
    @CacheEvict(cacheNames = "member")
    public void delete(String nickname) {
        ImageDeleteRequest imageDeleteRequest = new ImageDeleteRequest(
                List.of("male.png", "female.png"),
                "profile",
                nickname);

        s3Uploader.deleteFiles(imageDeleteRequest);
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
