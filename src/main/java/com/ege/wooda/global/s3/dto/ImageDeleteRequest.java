package com.ege.wooda.global.s3.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record ImageDeleteRequest(@NotBlank List<String> fileNames,
                                 @NotBlank String domain,
                                 @NotBlank String uuid) {

    public ImageDeleteRequest {}

    public List<String> getFileNamesWithPath() {
        return fileNames.stream()
                        .map(this::getPath)
                        .toList();
    }

    private String getPath(String fileName) {
        return "users/" + uuid + "/" + domain + "/" + fileName;
    }
}
