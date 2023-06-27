package com.ege.wooda.domain.profile.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ege.wooda.domain.member.domain.enums.Gender;
import com.ege.wooda.domain.profile.domain.Profile;
import com.ege.wooda.domain.profile.dto.param.ProfileCreateParam;
import com.ege.wooda.domain.profile.dto.param.ProfileUpdateParam;
import com.ege.wooda.domain.profile.dto.request.ProfileCreateRequest;
import com.ege.wooda.domain.profile.dto.request.ProfileUpdateRequest;
import com.ege.wooda.domain.profile.dto.response.ProfileDetailResponse;
import com.ege.wooda.domain.profile.dto.response.ProfileResponseMessage;
import com.ege.wooda.domain.profile.service.ProfileService;
import com.ege.wooda.global.common.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/{id}/profile")
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping("")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<ApiResponse<ProfileDetailResponse>> add(@PathVariable Long id,
                                                                  @RequestPart(value = "image",
                                                                          required = false) MultipartFile image,
                                                                  @RequestPart(value = "profileCreateRequest")
                                                                  ProfileCreateRequest profileCreateRequest)
            throws IOException {
        ProfileCreateParam profileCreateParam = ProfileCreateParam.builder()
                                                                  .memberId(id)
                                                                  .image(image)
                                                                  .nickname(profileCreateRequest.nickname())
                                                                  .firstDate(profileCreateRequest.firstDate())
                                                                  .gender(Gender.valueOf(
                                                                          profileCreateRequest.gender()))
                                                                  .build();

        Profile profile = profileService.save(profileCreateParam);

        return new ResponseEntity<>(
                ApiResponse.createSuccessWithData(ProfileResponseMessage.CREATE_PROFILE.getMessage(),
                                                  profile.toProfileDetailResponse()),
                HttpStatus.CREATED);
    }

    @GetMapping("")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<ApiResponse<ProfileDetailResponse>> details(@PathVariable Long id) {
        Profile profile = profileService.findProfileByMemberId(id);

        return ResponseEntity.ok(
                ApiResponse.createSuccessWithData(ProfileResponseMessage.READ_PROFILE.getMessage(),
                                                  profile.toProfileDetailResponse()));
    }

    @PutMapping("")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<ApiResponse<ProfileDetailResponse>> modify(@PathVariable Long id,
                                                                     @RequestPart(value = "image",
                                                                             required = false)
                                                                     MultipartFile image,
                                                                     @RequestPart(
                                                                             value = "profileUpdateRequest")
                                                                     ProfileUpdateRequest profileUpdateRequest)
            throws IOException {
        ProfileUpdateParam profileUpdateParam = ProfileUpdateParam.builder()
                                                                  .memberId(id)
                                                                  .image(image)
                                                                  .nickname(profileUpdateRequest.nickname())
                                                                  .firstDate(profileUpdateRequest.firstDate())
                                                                  .gender(Gender.valueOf(
                                                                          profileUpdateRequest.gender()))
                                                                  .build();
        Profile profile = profileService.update(profileUpdateParam);

        return ResponseEntity.ok(
                ApiResponse.createSuccessWithData(ProfileResponseMessage.UPDATE_PROFILE.getMessage(),
                                                  profile.toProfileDetailResponse()));
    }
}
