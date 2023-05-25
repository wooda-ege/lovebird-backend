package com.ege.wooda.global.s3.dto;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record ImageUploadRequest(@Nonnull List<MultipartFile> images,
                                 @NotBlank String domain,
                                 @NotBlank String username) {
    public ImageUploadRequest {}
}
