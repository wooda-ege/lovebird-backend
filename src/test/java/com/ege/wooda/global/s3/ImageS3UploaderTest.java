package com.ege.wooda.global.s3;

import com.ege.wooda.global.s3.config.S3MockConfig;

import com.ege.wooda.global.s3.dto.ImageDeleteRequest;
import com.ege.wooda.global.s3.dto.ImageUploadRequest;

import io.findify.s3mock.S3Mock;

import org.junit.After;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.Assert.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@Import(S3MockConfig.class)
public class ImageS3UploaderTest {

    @Autowired
    private ImageS3Uploader imageS3Uploader;
    @Autowired
    private S3Mock s3Mock;

    @After
    public void shutdownMockS3() {
        s3Mock.stop();
    }

    @Test
    @DisplayName("S3 이미지 업로드 테스트")
    public void uploadTest() throws IOException {
        // given
        List<MultipartFile> mockFiles = getMultipartFiles();
        ImageUploadRequest imageUploadRequest = new ImageUploadRequest(mockFiles, getImageNames(mockFiles),
                                                                       "test", "test");

        // when
        List<S3File> savedFiles = imageS3Uploader.upload(imageUploadRequest);

        // then
        assertEquals(imageUploadRequest.images().size(), savedFiles.size());
    }

    @Test
    @DisplayName("S3 이미지 삭제 테스트")
    public void deleteTest() throws IOException {
        // given
        List<MultipartFile> mockFiles = getMultipartFiles();
        ImageUploadRequest imageUploadRequest = new ImageUploadRequest(mockFiles, getImageNames(mockFiles),
                                                                       "test", "test");
        ImageDeleteRequest imageDeleteRequest = new ImageDeleteRequest(
                getMultipartFiles().stream()
                                   .map(MultipartFile::getOriginalFilename)
                                   .toList(),
                "test",
                "test");

        // when
        List<String> fileNames = imageS3Uploader.upload(imageUploadRequest).stream()
                                                .map(S3File::fileName)
                                                .toList();

        // then
        assertThatNoException()
                .isThrownBy(() -> imageS3Uploader.deleteFiles(imageDeleteRequest));
    }

    private List<String> getImageNames(List<MultipartFile> files) {
        return files.stream()
                    .map(MultipartFile::getOriginalFilename)
                    .toList();
    }

    private List<MultipartFile> getMultipartFiles() {
        String path1 = "test1.png";
        String contentType1 = "image/png";
        String path2 = "test2.png";
        String contentType2 = "image/png";

        return List.of(new MockMultipartFile("test1", path1, contentType1, "test1".getBytes())
                , new MockMultipartFile("test2", path2, contentType2, "test2".getBytes()));
    }
}