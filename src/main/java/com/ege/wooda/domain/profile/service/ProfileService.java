package com.ege.wooda.domain.profile.service;

import static java.util.Objects.isNull;

import java.io.IOException;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ege.wooda.domain.profile.domain.Profile;
import com.ege.wooda.domain.profile.dto.param.ProfileCreateParam;
import com.ege.wooda.domain.profile.dto.param.ProfileUpdateParam;
import com.ege.wooda.domain.profile.repository.ProfileRepository;
import com.ege.wooda.global.s3.ImageS3Uploader;
import com.ege.wooda.global.s3.dto.S3File;
import com.ege.wooda.global.common.enums.DomainName;
import com.ege.wooda.global.s3.dto.ImageDeleteRequest;
import com.ege.wooda.global.s3.dto.ImageUploadRequest;
import com.ege.wooda.global.s3.fomatter.FileNameFormatter;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ImageS3Uploader imageS3Uploader;
    private final FileNameFormatter fileNameFormatter;

    @Transactional
    public Profile save(ProfileCreateParam param) throws IOException {
        Profile profile = profileRepository.save(param.toEntity());

        if (!isNull(param.image())) {
            ImageUploadRequest imageUploadRequest = getImageUploadRequest(param.image(), param.memberId());

            profile.updateImageUrl(uploadImage(imageUploadRequest).get(0));
        }

        return profile;
    }

    @Transactional
    @CacheEvict(cacheNames = "profile", key = "#param.memberId()")
    public Profile update(ProfileUpdateParam param) throws IOException {
        Profile profile = findProfileByMemberId(param.memberId());

        profile.update(param.toEntity());

        if (!isNull(param.image())) {
            ImageUploadRequest imageUploadRequest = getImageUploadRequest(param.image(), param.memberId());

            profile.updateImageUrl(uploadImage(imageUploadRequest).get(0));
        }

        return profile;
    }

    @Transactional
    @CacheEvict(cacheNames = "profile", key = "#memberId")
    public void delete(Long memberId) {
        Profile profile = findProfileByMemberId(memberId);

        if (!isNull(profile.getImageUrl())) {
            ImageDeleteRequest imageDeleteRequest = getImageDeleteRequest(memberId, profile.getImageUrl());

            imageS3Uploader.deleteFiles(imageDeleteRequest);
        }

        profileRepository.delete(profile);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "profile", key = "#memberId", value = "profile")
    public Profile findProfileByMemberId(Long memberId) {
        return profileRepository.findProfileByMemberId(memberId).orElseThrow(EntityNotFoundException::new);
    }

    public List<String> uploadImage(ImageUploadRequest imageUploadRequest) throws IOException {
        return imageS3Uploader.upload(imageUploadRequest).stream()
                              .map(S3File::fileUrl)
                              .toList();
    }

    private ImageUploadRequest getImageUploadRequest(MultipartFile image, Long memberId) {
        String imageName = fileNameFormatter.generateImageName(image, memberId);

        return ImageUploadRequest.builder()
                                 .images(List.of(image))
                                 .imageNames(List.of(imageName))
                                 .domain(DomainName.MEMBER.getDomain())
                                 .memberId(memberId)
                                 .build();
    }

    private ImageDeleteRequest getImageDeleteRequest(Long memberId, String imageUrl) {
        String extension = fileNameFormatter.getFileExtension(imageUrl);

        return new ImageDeleteRequest(
                List.of(memberId + "-profile" + extension),
                DomainName.MEMBER.getDomain(),
                memberId);
    }

    //    public void checkDuplicatedNickname(String oldNickname, String newNickname) {
//        if(StringUtils.equals(oldNickname, newNickname)) { return; }
//
//        memberRepository.findMemberByNickname(newNickname).ifPresent(member -> {
//            try {
//                throw new SQLIntegrityConstraintViolationException();
//            } catch (SQLIntegrityConstraintViolationException e) {
//                throw new DataIntegrityViolationException(e.getMessage());
//            }
//        });
//    }
}
