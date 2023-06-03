package com.ege.wooda.domain.member.service;

import com.ege.wooda.domain.member.domain.Gender;
import com.ege.wooda.domain.member.domain.Member;
import com.ege.wooda.domain.member.dto.request.MemberCreateRequest;
import com.ege.wooda.domain.member.dto.request.MemberUpdateRequest;
import com.ege.wooda.domain.member.repository.MemberRepository;
import com.ege.wooda.global.s3.ImageS3Uploader;
import com.ege.wooda.global.s3.S3File;
import com.ege.wooda.global.s3.fomatter.FileNameFormatter;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ImageS3Uploader imageS3Uploader;

    @Mock
    private FileNameFormatter fileNameFormatter;

    @AfterEach
    public void cleanup() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("Member를 생성하면 해당 Member의 ID가 반환된다")
    void save() throws IOException {
        // given
        Member mockMember = getMember("홍길동", Gender.MALE, getLocalDate("2023-05-15"));
        Long mockId = 1L;
        List<MultipartFile> mockImages = getMultipartFiles();
        List<S3File> mockS3File = getS3File();

        MemberCreateRequest memberCreateRequest = MemberCreateRequest.builder()
                                                                     .nickname("홍길동")
                                                                     .firstDate("2023-05-15")
                                                                     .gender("MALE")
                                                                     .build();

        ReflectionTestUtils.setField(mockMember, "id", mockId);
        given(imageS3Uploader.upload(any()))
                .willReturn(mockS3File);
        given(memberRepository.save(any()))
                .willReturn(mockMember);

        // when
        Long saveMemberId = memberService.save(mockImages, memberCreateRequest);

        // then
        assertEquals(saveMemberId, mockId);
    }

    @Test
    @DisplayName("해당 ID의 Member를 반환한다.")
    void findById() {
        // given
        Member mockMember = getMember("홍길동", Gender.MALE, getLocalDate("2023-05-15"));
        Long mockId = 1L;

        ReflectionTestUtils.setField(mockMember, "id", mockId);
        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(mockMember));

        // when
        memberRepository.save(mockMember);

        // then
        Member findMember = memberService.findById(mockId);

        assertEquals(findMember, mockMember);
    }

    @Test
    @DisplayName("Member의 정보를 수정하면 해당 Member의 ID가 반환된다.")
    void update() throws IOException {
        // given
        Member mockMember = getMember("홍길동", Gender.MALE, getLocalDate("2023-05-15"));

        String updateNickname = "청길동";
        String updateGender = "FEMALE";
        String updateFirstDate = "2021-04-16";
        String mockNickname = "홍길동";

        MemberUpdateRequest memberUpdateRequest = MemberUpdateRequest.builder()
                                                                     .nickname(updateNickname)
                                                                     .gender(updateGender)
                                                                     .firstDate(updateFirstDate)
                                                                     .build();

        ReflectionTestUtils.setField(mockMember, "nickname", mockNickname);
        given(memberRepository.findMemberByNickname(anyString()))
                .willReturn(Optional.empty())
                .willReturn(Optional.of(mockMember));

        // when
        Long updateId = memberService.update(mockNickname, new ArrayList<>(), memberUpdateRequest);

        // then
        Long findId = memberService.findMemberByNickname(mockNickname).getId();

        assertEquals(updateId, findId);
    }

    @Test
    void deleteSuccess() {
        // given
        String mockNickname = "홍길동";
        Member mockMember = getMember(mockNickname, Gender.MALE, getLocalDate("2023-05-15"));

        given(memberRepository.findMemberByNickname(anyString()))
                .willReturn(Optional.of(mockMember));

        // then
        assertDoesNotThrow(() -> memberService.delete(mockNickname));
    }

    @Test
    void deleteFail() {
        // given
        given(memberRepository.findMemberByNickname(anyString()))
                .willReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class, () -> memberService.delete("청길동"));
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

    private List<Member> getMemberList() {
        return List.of(getMember("홍길동", Gender.MALE, getLocalDate("2023-05-09"))
                , getMember("청길동", Gender.FEMALE, getLocalDate("2023-05-10"))
                , getMember("녹길동", Gender.MALE, getLocalDate("2021-04-16")));
    }

    private List<MultipartFile> getMultipartFiles() {
        String path1 = "male.png";
        String contentType1 = "image/png";
        String path2 = "female.png";
        String contentType2 = "image/png";

        return List.of(new MockMultipartFile("male", path1, contentType1, "male".getBytes())
                , new MockMultipartFile("female", path2, contentType2, "female".getBytes()));
    }

    private List<S3File> getS3File() {
        return List.of(new S3File("male.png",
                                  "https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&prefix=member/홍길동/male.png"),
                       new S3File("male.png",
                                  "https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&prefix=member/홍길동/female.png"));
    }

    private LocalDate getLocalDate(String firstDate) {
        return LocalDate.parse(firstDate, DateTimeFormatter.ISO_DATE);
    }
}