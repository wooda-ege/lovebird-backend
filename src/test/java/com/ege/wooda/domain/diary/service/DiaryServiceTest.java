package com.ege.wooda.domain.diary.service;

import com.ege.wooda.domain.diary.Diary;
import com.ege.wooda.domain.diary.dto.request.DiaryCreateRequest;
import com.ege.wooda.domain.diary.repository.DiaryRepository;
import com.ege.wooda.domain.member.domain.Gender;
import com.ege.wooda.domain.member.domain.Member;
import com.ege.wooda.domain.member.repository.MemberRepository;
import com.ege.wooda.domain.member.service.MemberService;
import com.ege.wooda.global.config.jpa.JpaConfig;
import com.ege.wooda.global.s3.ImageS3Uploader;
import com.ege.wooda.global.s3.S3File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@Import(JpaConfig.class)
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@DataJpaTest
public class DiaryServiceTest {

    @InjectMocks
    private DiaryService diaryService;

    @Mock
    private DiaryRepository diaryRepository;

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ImageS3Uploader imageS3Uploader;

    @AfterEach
    public void cleanup() {
        memberRepository.deleteAll();
        diaryRepository.deleteAll();
    }

    @Test
    @DisplayName("Diary를 생성하면 해당 Diary의 ID를 반환한다.")
    public void save() throws IOException{
        List<String> urls1 = new ArrayList<>();
        urls1.add("https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&amp;prefix=member/홍길동/1-1.png");
        urls1.add("https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&amp;prefix=member/홍길동/1-2.png");

        Member mockMember = getMember("홍길동", Gender.MALE, getLocalDate("2023-05-09"));

        List<MultipartFile> mockImgs=getMultipartFiles();
        List<S3File> mockS3File=getS3Files();

        memberRepository.save(mockMember);

        Diary mockDiary=getDiary(1L, "Test Diary1", "Test diary subtitle1", getLocalDate("2023-05-28"), "place1", "contents1", urls1);
        Long mockId=1L;

        DiaryCreateRequest diaryCreateRequest=DiaryCreateRequest.builder()
                .memberId(1L)
                .title("Test Diary1")
                .sub_title("Test diary subtitle1")
                .memory_date(getLocalDate("2023-05-28"))
                .place("place1")
                .contents("contents1")
                .imgUrls(urls1)
                .build();

        ReflectionTestUtils.setField(mockDiary,"memberId",mockId);
        given(imageS3Uploader.upload(any()))
                .willReturn(mockS3File);
        given(diaryRepository.save(any()))
                .willReturn(mockDiary);

        System.out.println(memberRepository.findById(mockMember.getId()));

        Long saveDiaryId=diaryService.save(mockImgs, diaryCreateRequest);

        assertEquals(saveDiaryId, mockId);
    }

    private Member getMember(String nickname, Gender gender, LocalDate firstDate) {
        return Member.builder()
                .nickname(nickname)
                .firstDate(firstDate)
                .gender(gender)
                .pictureM(null)
                .pictureW(null)
                .build();
    }

    private Diary getDiary(Long memberId, String title, String subTitle, LocalDate memoryDate, String place, String contents, List imgUrls){
        return Diary.builder()
                .memberId(memberId)
                .title(title)
                .subTitle(subTitle)
                .memoryDate(memoryDate)
                .place(place)
                .contents(contents)
                .imgUrls(imgUrls)
                .build();
    }

    private List<Diary> getDiaryList(){
        List<String> urls1 = new ArrayList<String>();
        urls1.add("https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&amp;prefix=member/홍길동/1-1.png");
        urls1.add("https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&amp;prefix=member/홍길동/1-2.png");

        List<String> urls2 = new ArrayList<String>();
        urls2.add("https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&amp;prefix=member/여혜민/2-1.png");
        urls2.add("https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&amp;prefix=member/여혜민/2-2.png");
        urls2.add("https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&amp;prefix=member/여혜민/2-3.png");

        return List.of(getDiary(1L, "Test Diary1", "Test diary subtitle1", LocalDate.now(), "place1", "contents1", urls1)
                ,getDiary(2L, "Test Diary2", "Test diary subtitle2", LocalDate.now(), "place2", "contents2", urls2)
                ,getDiary(2L, "Test Diary3", "Test diary subtitle3", LocalDate.now(), "place3", "contents3", urls2));
    }

    private List<MultipartFile> getMemberMultipartFiles() {
        String path1 = "male.png";
        String contentType1 = "image/png";
        String path2 = "female.png";
        String contentType2 = "image/png";

        return List.of(new MockMultipartFile("male", path1, contentType1, "male".getBytes())
                , new MockMultipartFile("female", path2, contentType2, "female".getBytes()));
    }

    private List<MultipartFile> getMultipartFiles(){
        String path1="1-1.png";
        String path2="1-2.png";

        return List.of(new MockMultipartFile("1-1",path1,"image/png","1-1".getBytes()), new MockMultipartFile("1-2",path2,"image/png","1-2".getBytes()));

    }

    private List<S3File> getS3Files(){
        String path1="1-1.png";
        String path2="1-2.png";

        return List.of(new S3File("1-1.png","https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&amp;prefix=member/홍길동/1-1.png"),new S3File("1-2.png","https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&amp;prefix=member/홍길동/1-2.png"));
    }

    private LocalDate getLocalDate(String firstDate) {
        return LocalDate.parse(firstDate, DateTimeFormatter.ISO_DATE);
    }
}
