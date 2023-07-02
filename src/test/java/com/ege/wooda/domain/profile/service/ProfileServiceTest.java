package com.ege.wooda.domain.profile.service;

import com.ege.wooda.domain.member.domain.enums.Gender;
import com.ege.wooda.domain.profile.domain.Profile;
import com.ege.wooda.domain.profile.dto.param.ProfileCreateParam;
import com.ege.wooda.domain.profile.dto.param.ProfileUpdateParam;
import com.ege.wooda.domain.profile.repository.ProfileRepository;
import com.ege.wooda.global.s3.ImageS3Uploader;
import com.ege.wooda.global.s3.dto.S3File;
import com.ege.wooda.global.s3.fomatter.FileNameFormatter;

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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @InjectMocks
    private ProfileService profileService;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private ImageS3Uploader imageS3Uploader;

    @Mock
    private FileNameFormatter fileNameFormatter;

    @AfterEach
    public void cleanup() {
        profileRepository.deleteAll();
    }

    @Test
    @DisplayName("Member를 생성하면 해당 Member의 ID가 반환된다")
    void save() throws IOException {
        // give
        String mockNickname = "홍길동";
        String mockFirstDate = "2023-05-15";
        Profile mockProfile = getProfile(1L, mockNickname, getLocalDate(mockFirstDate), Gender.MALE);
        MultipartFile mockImage = getMultipartFile();
        List<S3File> mockS3File = getS3File(1L);

        Long mockId = 1L;
        ReflectionTestUtils.setField(mockProfile, "id", mockId);

        given(fileNameFormatter.generateImageName(any(), anyLong()))
                .willReturn(mockId + "-profile.png");
        given(profileRepository.save(any()))
                .willReturn(mockProfile);
        given(imageS3Uploader.upload(any()))
                .willReturn(mockS3File);

        // when
        ProfileCreateParam param = ProfileCreateParam.builder()
                                                     .memberId(1L)
                                                     .image(mockImage)
                                                     .nickname(mockNickname)
                                                     .firstDate(mockFirstDate)
                                                     .gender(Gender.MALE)
                                                     .build();
        Profile saveMember = profileService.save(param);

        // then
        assertEquals(saveMember, mockProfile);
    }

    @Test
    @DisplayName("해당 Member ID의 Profile을 반환한다.")
    void findById() {
        // given
        Long mockMemberId = 1L;
        Profile mockProfile = getProfile(mockMemberId, "홍길동", getLocalDate("2023-06-24"), Gender.UNKNOWN);

        given(profileRepository.findProfileByMemberId(anyLong()))
                .willReturn(Optional.of(mockProfile));

        // when
        profileRepository.save(mockProfile);

        // then
        Profile findProfile = profileService.findProfileByMemberId(mockMemberId);

        assertEquals(findProfile, mockProfile);
    }

    @Test
    @DisplayName("Profile 데이터를 수정한다.")
    void update() throws IOException {
        // given
        String mockNickname = "홍길동";
        String mockFirstDate = "2023-05-15";
        Profile mockProfile = getProfile(1L, mockNickname, getLocalDate(mockFirstDate), Gender.MALE);
        Profile mockChangeProfile = getProfile(1L, "청길동", getLocalDate(mockFirstDate), Gender.UNKNOWN);
        MultipartFile mockImage = getMultipartFile();
        List<S3File> mockS3File = getS3File(1L);

        Long mockId = 1L;
        ReflectionTestUtils.setField(mockProfile, "id", mockId);
        given(fileNameFormatter.generateImageName(any(), anyLong()))
                .willReturn(mockId + "-profile.png");
        given(profileRepository.findProfileByMemberId(any()))
                .willReturn(Optional.of(mockChangeProfile));
        given(imageS3Uploader.upload(any()))
                .willReturn(mockS3File);

        // when
        ProfileUpdateParam param = ProfileUpdateParam.builder()
                                                     .memberId(1L)
                                                     .image(mockImage)
                                                     .nickname(mockNickname)
                                                     .firstDate(mockFirstDate)
                                                     .gender(Gender.MALE)
                                                     .build();
        Profile updateProfile = profileService.update(param);

        // then
        Profile findProfile = profileService.findProfileByMemberId(mockId);

        assertEquals(findProfile, updateProfile);
    }

    private Profile getProfile(Long memberId, String nickname, LocalDate firstDate, Gender gender) {
        return Profile.builder()
                      .memberId(memberId)
                      .nickname(nickname)
                      .firstDate(firstDate)
                      .gender(gender)
                      .build();
    }

    private MockMultipartFile getMultipartFile() {
        String path = "image.png";
        String contentType = "image/png";

        return new MockMultipartFile("image.png", path, contentType, "image".getBytes());
    }

    private List<S3File> getS3File(Long memberId) {
        return List.of(new S3File(memberId + "-profile.png",
                                  "https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&prefix=member/users"
                                  + memberId + "/profile" + memberId + "-profile.png"));
    }

    private LocalDate getLocalDate(String firstDate) {
        return LocalDate.parse(firstDate, DateTimeFormatter.ISO_DATE);
    }
}