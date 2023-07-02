package com.ege.wooda.domain.diary.service;

import com.ege.wooda.domain.diary.domain.Diary;
import com.ege.wooda.domain.diary.dto.request.DiaryCreateRequest;
import com.ege.wooda.domain.diary.dto.request.DiaryUpdateRequest;
import com.ege.wooda.domain.diary.repository.DiaryRepository;
import com.ege.wooda.global.config.jpa.JpaConfig;
import com.ege.wooda.global.s3.ImageS3Uploader;
import com.ege.wooda.global.s3.dto.S3File;
import com.ege.wooda.global.s3.fomatter.FileNameFormatter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@Import(JpaConfig.class)
@ExtendWith(MockitoExtension.class)
public class DiaryServiceTest {

    @InjectMocks
    private DiaryService diaryService;

    @Mock
    private DiaryRepository diaryRepository;

    @Mock
    private ImageS3Uploader imageS3Uploader;

    @Mock
    private FileNameFormatter fileNameFormatter;

    @AfterEach
    public void cleanup() {
        diaryRepository.deleteAll();
    }

    @Test
    @DisplayName("Diary를 생성하면 해당 Diary의 ID를 반환한다.")
    public void save() throws Exception {
        // given
        List<String> urls = getImageUrls("홍길동");
        List<MultipartFile> mockImages = getMultipartFiles();
        List<S3File> mockS3File = getS3Files();
        Diary mockDiary = getDiary(1L, "Test Diary1", "Test diary subtitle1", getLocalDate("2023-05-28"),
                                   "place1", "contents1", urls);
        Long mockId = 1L;

        // when
        DiaryCreateRequest diaryCreateRequest = DiaryCreateRequest.builder()
                                                                  .title("Test Diary1")
                                                                  .subTitle("Test diary subtitle1")
                                                                  .memoryDate("2023-05-28")
                                                                  .place("place1")
                                                                  .content("contents1")
                                                                  .build();

        ReflectionTestUtils.setField(mockDiary, "id", mockId);
        given(imageS3Uploader.upload(any()))
                .willReturn(mockS3File);
        given(diaryRepository.save(any()))
                .willReturn(mockDiary);

        Long saveDiaryId = diaryService.save(mockImages, diaryCreateRequest, mockId);

        assertEquals(saveDiaryId, mockId);
    }

    @Test
    @DisplayName("모든 Diary를 반환한다.")
    public void findAll() {
        List<Diary> diaryList = getDiaryList();
        List<Long> mockIdList = new ArrayList<>(Arrays.asList(1L, 2L, 3L));
        ReflectionTestUtils.setField(diaryList.get(0), "id", mockIdList.get(0));
        ReflectionTestUtils.setField(diaryList.get(1), "id", mockIdList.get(1));
        ReflectionTestUtils.setField(diaryList.get(2), "id", mockIdList.get(2));

        given(diaryRepository.findAll())
                .willReturn(diaryList);

        List<Diary> diaries = diaryService.findDiaries();

        assertEquals(diaries, diaryList);

    }

    @Test
    @DisplayName("Diary의 ID를 통해 Diary를 반환한다.")
    public void findById() {
        List<String> urls2 = getImageUrls("홍길동");

        Diary mockDiary = getDiary(2L, "Test Diary2", "Test diary subtitle2", getLocalDate("2023-05-30"),
                                   "place2", "contents2", urls2);
        Long mockId = 2L;

        ReflectionTestUtils.setField(mockDiary, "id", mockId);
        given(diaryRepository.findById(anyLong()))
                .willReturn(Optional.of(mockDiary));

        Diary findDiary = diaryService.findById(mockId);

        assertEquals(findDiary, mockDiary);

    }

    @Test
    @DisplayName("Diary 정보를 수정하면 해당 Diary의 ID가 반환된다.")
    public void update() throws IOException {
        // given
        List<String> urls2 = getImageUrls("홍길동");
        Diary mockDiary = getDiary(2L, "Test Diary2", "Test diary subtitle2", getLocalDate("2023-05-30"),
                                   "place2", "contents2", urls2);
        Long mockId = 3L;
        String updateTitle = "Test Diary3";

        List<MultipartFile> mockImgs = getMultipartFiles();
        List<S3File> mockS3File = getS3Files();

        DiaryUpdateRequest diaryUpdateRequest = DiaryUpdateRequest.builder()
                                                                  .title(updateTitle)
                                                                  .memoryDate("2023-06-01")
                                                                  .build();

        ReflectionTestUtils.setField(mockDiary, "id", mockId);

        given(diaryRepository.findById(anyLong()))
                .willReturn(Optional.of(mockDiary));

        // when
        Long updateId = diaryService.update(mockId, mockImgs, diaryUpdateRequest, mockId);

        // then
        Long findId = diaryService.findById(mockId).getId();
        assertEquals(updateId, findId);
    }

    @Test
    @DisplayName("Member ID와 Diary ID를 통해 Diary 삭제에 성공한다.")
    public void delete() {
        // given
        Long mockMemberId = 2L;
        Long mockDiaryId = 2L;
        List<String> urls2 = getImageUrls("홍길동");
        Diary mockDiary = getDiary(mockMemberId, "Test Diary2", "Test diary subtitle2", getLocalDate("2023-05-30"),
                                   "place2", "contents2", urls2);

        given(diaryRepository.findDiariesByMemberIdAndId(anyLong(), anyLong()))
                .willReturn(Optional.of(mockDiary));

        // then
        assertDoesNotThrow(() -> diaryService.delete(mockMemberId, mockDiaryId));
    }

    private Diary getDiary(Long memberId, String title, String subTitle, LocalDate memoryDate, String place,
                           String content, List<String> imgUrls) {
        return Diary.builder()
                    .memberId(memberId)
                    .title(title)
                    .subTitle(subTitle)
                    .memoryDate(memoryDate)
                    .place(place)
                    .content(content)
                    .imgUrls(imgUrls)
                    .build();
    }

    private List<Diary> getDiaryList() {
        List<String> urls1 = getImageUrls("홍길동");

        List<String> urls2 = getImageUrls("여혜민");

        return List.of(getDiary(1L, "Test Diary1", "Test diary subtitle1", getLocalDate("2023-06-01"), "place1",
                                "contents1", urls1)
                , getDiary(2L, "Test Diary2", "Test diary subtitle2", getLocalDate("2023-06-01"), "place2",
                           "contents2", urls2)
                , getDiary(3L, "Test Diary3", "Test diary subtitle3", getLocalDate("2023-06-01"), "place3",
                           "contents3", urls2));
    }

    private List<String> getImageUrls(String name) {
        String url1 =
                "https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&amp;prefix=member/"
                + name + "/1-1.png";
        String url2 =
                "https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&amp;prefix=member/"
                + name + "/1-2.png";

        return new ArrayList<>(List.of(url1, url2));
    }

    private List<MultipartFile> getMemberMultipartFiles() {
        String name = "홍길순";

        return List.of(new MockMultipartFile("1-1", name, "image/png", "1-1".getBytes())
                , new MockMultipartFile("1-2", name, "image/png", "1-2".getBytes()));
    }

    private List<MultipartFile> getMultipartFiles() {
        String path1 = "1-1.png";
        String path2 = "1-2.png";

        return List.of(new MockMultipartFile("1-1", path1, "image/png", "1-1".getBytes()),
                       new MockMultipartFile("1-2", path2, "image/png", "1-2".getBytes()));

    }

    private List<S3File> getS3Files() {
        String path1 = "1-1.png";
        String path2 = "1-2.png";

        return List.of(new S3File("1-1.png",
                                  "https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&amp;prefix=member/홍길동/1-1.png"),
                       new S3File("1-2.png",
                                  "https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&amp;prefix=member/홍길동/1-2.png"));
    }

    private LocalDate getLocalDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
    }

    private String getUUID() {
        return UUID.randomUUID().toString();
    }
}
