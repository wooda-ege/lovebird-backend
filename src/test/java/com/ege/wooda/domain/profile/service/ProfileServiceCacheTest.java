//package com.ege.wooda.domain.profile.service;
//
//import com.ege.wooda.domain.member.domain.enums.Gender;
//import com.ege.wooda.domain.profile.domain.Profile;
//import com.ege.wooda.domain.profile.repository.ProfileRepository;
//import com.ege.wooda.global.config.cache.CacheConfig;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//import org.springframework.context.annotation.Import;
//import org.springframework.mock.web.MockMultipartFile;
//
//import java.io.IOException;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.Optional;
//import java.util.stream.IntStream;
//
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//
//@Import(CacheConfig.class)
//@SpringBootTest
//public class ProfileServiceCacheTest {
//
//    @Autowired
//    private ProfileService profileService;
//
//    @MockBean
//    private ProfileRepository profileRepository;
//
//    @AfterEach
//    public void cleanup() {
//        profileRepository.deleteAll();
//    }
//
//    @Test
//    @DisplayName("findProfileByMemberId() 메서드를 호출하면 처음을 제외한 나머지는 Cache가 인터셉트한다.")
//    void findProfileByMemberIdUsedCache() throws IOException {
//        // given
//        Long mockMemberId = 1L;
//        Profile mockProfile = getProfile(mockMemberId, "홍길동", getLocalDate("2023-06-24"), Gender.UNKNOWN);
//
//        given(profileRepository.findProfileByMemberId(anyLong()))
//                .willReturn(Optional.of(mockProfile));
//
//        // when
//        IntStream.range(0, 10).forEach((i) -> profileService.findProfileByMemberId(mockMemberId));
//
//        // then
//        verify(profileRepository, times((1))).findProfileByMemberId(mockMemberId);
//    }
//
//    private Profile getProfile(Long memberId, String nickname, LocalDate firstDate, Gender gender) {
//        return Profile.builder()
//                      .memberId(memberId)
//                      .nickname(nickname)
//                      .firstDate(firstDate)
//                      .gender(gender)
//                      .build();
//    }
//
//    private MockMultipartFile getMultipartFile() {
//        String path = "image.png";
//        String contentType = "image/png";
//
//        return new MockMultipartFile("image.png", path, contentType, "image".getBytes());
//    }
//
//    private LocalDate getLocalDate(String firstDate) {
//        return LocalDate.parse(firstDate, DateTimeFormatter.ISO_DATE);
//    }
//}
