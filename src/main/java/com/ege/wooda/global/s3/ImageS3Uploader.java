package com.ege.wooda.global.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.ege.wooda.global.s3.dto.ImageDeleteRequest;
import com.ege.wooda.global.s3.dto.ImageUploadRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ImageS3Uploader  {
    @Value("${cloud.aws.s3-bucket}")
    @Getter
    private String s3Bucket;

    private final AmazonS3 amazonS3Client;

    public List<S3File> upload(ImageUploadRequest imageUploadRequest) throws IOException {
        List<S3File> s3Files = new ArrayList<>();

        for (MultipartFile image : imageUploadRequest.images()) {
            String fileName = createFileName(
                    imageUploadRequest.domain(),
                    imageUploadRequest.username(),
                    image.getOriginalFilename());
            s3Files.add(new S3File(fileName, putS3(image, fileName, getObjectMetadata(image))));
        }

        return s3Files;
    }

    public void deleteFiles(ImageDeleteRequest imageDeleteRequest) {
        List<String> fileNames = imageDeleteRequest.getFileNamesWithPath();
        fileNames.forEach(this::deleteFromS3);
    }

    private void deleteFromS3(String fileName) {
        validateFromS3(fileName);
        amazonS3Client.deleteObject(new DeleteObjectRequest(s3Bucket, fileName));
    }

    private void validateFromS3(String fileName) {
        if(!amazonS3Client.doesObjectExist(s3Bucket, fileName)) {
            throw new AmazonS3Exception("Object " + fileName + " does not exist");
        }
    }

    private String putS3(MultipartFile multipartFile, String fileName, ObjectMetadata objectMetadata) throws IOException {
        amazonS3Client.putObject(
                new PutObjectRequest(s3Bucket, fileName, multipartFile.getInputStream(), objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
        return getS3Url(fileName);
    }

    private String getS3Url(String fileName) {
        return amazonS3Client.getUrl(s3Bucket, fileName).toString();
    }

    private String createFileName(String domain, String username, String originalName) {
        return "users/" + username + "/" + domain + "/" + originalName;
    }

    private ObjectMetadata getObjectMetadata(MultipartFile multipartFile) {
        ObjectMetadata objectMetaData = new ObjectMetadata();
        objectMetaData.setContentType(multipartFile.getContentType());
        objectMetaData.setContentLength(multipartFile.getSize());

        return objectMetaData;
    }
}