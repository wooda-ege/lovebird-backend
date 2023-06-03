package com.ege.wooda.global.s3.fomatter;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class FileNameFormatter {

    // 로그인 구현 시 변경 예정
    public List<String> generateImageNames(List<MultipartFile> images, String uuid) {
        return List.of(
                uuid + "-male" + getFileExtension(Objects.requireNonNull(images.get(0).getOriginalFilename())),
                uuid + "-female" + getFileExtension(Objects.requireNonNull(images.get(1).getOriginalFilename()))
        );
    }

    public List<String> generateImageNames(List<MultipartFile> images, String uuid, Long diaryId) {
        List<String> imageNames = new ArrayList<>();
        for (int i = 1; i <= images.size(); i++) {
            imageNames.add(uuid +
                           "_" +
                           diaryId +
                           "-" +
                           i +
                           getFileExtension(Objects.requireNonNull(images.get(i).getOriginalFilename())));
        }
        return imageNames;
    }

    public String getFileExtension(String originalFileName) {
        return originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
    }
}
