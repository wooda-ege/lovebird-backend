package com.ege.wooda.global.s3.fomatter;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class FileNameFormatter {
    public String generateImageName(MultipartFile image, Long memberId) {
        return memberId + "-profile" + getFileExtension(Objects.requireNonNull(image.getOriginalFilename()));
    }

    public List<String> generateImageNames(List<MultipartFile> images, Long memberId, Long diaryId) {
        List<String> imageNames = new ArrayList<>();
        for (int i = 1; i <= images.size(); i++) {
            imageNames.add(memberId +
                           "_" +
                           diaryId +
                           "-" +
                           i +
                           getFileExtension(Objects.requireNonNull(images.get(i - 1).getOriginalFilename())));
        }
        return imageNames;
    }

    public String getFileExtension(String originalFileName) {
        return "." + originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
    }
}
