package com.ege.wooda.domain.member.service;

import com.ege.wooda.domain.member.domain.Member;
import com.ege.wooda.domain.member.dto.request.MemberCreateRequest;
import com.ege.wooda.domain.member.dto.request.MemberUpdateRequest;
import com.ege.wooda.domain.member.repository.MemberRepository;
import com.ege.wooda.global.s3.ImageS3Uploader;
import com.ege.wooda.global.s3.S3File;
import com.ege.wooda.global.s3.dto.DomainName;
import com.ege.wooda.global.s3.dto.ImageDeleteRequest;
import com.ege.wooda.global.s3.dto.ImageUploadRequest;
import com.ege.wooda.global.s3.fomatter.FileNameFormatter;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.apache.commons.codec.binary.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final ImageS3Uploader s3Uploader;
    private final FileNameFormatter fileNameFormatter;

    @Transactional
    public Long save(List<MultipartFile> images, MemberCreateRequest memberCreateRequest) throws IOException {
        checkDuplicatedNickname("", memberCreateRequest.nickname());
        String uuid = getUUID();

        ImageUploadRequest imageUploadRequest = getImageUploadRequest(images, uuid);

        List<String> imageUrls = s3Uploader.upload(imageUploadRequest).stream()
                                           .map(S3File::fileUrl)
                                           .toList();
        Member member = memberCreateRequest.toEntity(imageUrls, uuid);

        return memberRepository.save(member).getId();
    }

    @Transactional
    @CacheEvict(cacheNames = "member", key = "#nickname")
    public Long update(String nickname, List<MultipartFile> images, MemberUpdateRequest memberUpdateRequest)
            throws IOException {
        checkDuplicatedNickname(nickname, memberUpdateRequest.nickname());

        Member member = findMemberByNickname(nickname);
        if (!images.isEmpty()) {
            ImageDeleteRequest imageDeleteRequest = getImageDeleteRequest(member.getUuid());

            s3Uploader.deleteFiles(imageDeleteRequest);

            ImageUploadRequest imageUploadRequest = getImageUploadRequest(images, member.getUuid());
            s3Uploader.upload(imageUploadRequest);
        }

        member.update(memberUpdateRequest.toEntity());

        return member.getId();
    }

    @Transactional
    @CacheEvict(cacheNames = "member", key = "#nickname")
    public void delete(String nickname) {
        Member member = findMemberByNickname(nickname);
        String uuid = member.getUuid();
        member.getPictureM();

        String extensionM = fileNameFormatter.getFileExtension(member.getPictureM());
        String extensionW = fileNameFormatter.getFileExtension(member.getPictureW());

        ImageDeleteRequest imageDeleteRequest = getImageDeleteRequest(uuid, extensionM, extensionW);

        s3Uploader.deleteFiles(imageDeleteRequest);
        memberRepository.delete(findMemberByNickname(nickname));
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "member", key = "#nickname", value = "member")
    public Member findMemberByNickname(String nickname) {
        return memberRepository.findMemberByNickname(nickname).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Member findById(Long id) {
        return memberRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    private String getUUID() {
        return UUID.randomUUID().toString();
    }

    public void checkDuplicatedNickname(String oldNickname, String newNickname) {
        if(StringUtils.equals(oldNickname, newNickname)) { return; }

        memberRepository.findMemberByNickname(newNickname).ifPresent(member -> {
            try {
                throw new SQLIntegrityConstraintViolationException();
            } catch (SQLIntegrityConstraintViolationException e) {
                throw new DataIntegrityViolationException(e.getMessage());
            }
        });
    }

    private ImageUploadRequest getImageUploadRequest(List<MultipartFile> images, String uuid) {
        return ImageUploadRequest.builder()
                                 .images(images)
                                 .imageNames(fileNameFormatter.generateImageNames(images, uuid))
                                 .domain(DomainName.MEMBER.getDomain())
                                 .uuid(uuid)
                                 .build();
    }

    private ImageDeleteRequest getImageDeleteRequest(String uuid, String extensionM, String extensionW) {
        return new ImageDeleteRequest(
                List.of(uuid + "-male" + extensionM, uuid + "-female" + extensionW),
                DomainName.MEMBER.getDomain(),
                uuid);
    }
}
