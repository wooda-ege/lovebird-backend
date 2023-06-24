package com.ege.wooda.global.s3.dto;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record ImageUploadRequest(@Nonnull List<MultipartFile> images,
                                 @Nonnull List<String> imageNames,
                                 @NotBlank String domain,
                                 @NotBlank Long memberId) {
    @Builder
    public ImageUploadRequest {}
}
