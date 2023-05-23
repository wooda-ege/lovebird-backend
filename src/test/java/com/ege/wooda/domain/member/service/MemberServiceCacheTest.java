package com.ege.wooda.domain.member.service;

import com.ege.wooda.domain.member.domain.Gender;
import com.ege.wooda.domain.member.domain.Member;
import com.ege.wooda.domain.member.repository.MemberRepository;
import com.ege.wooda.global.config.cache.CacheConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Import(CacheConfig.class)
@SpringBootTest
public class MemberServiceCacheTest {

    @Autowired
    private MemberService memberService;

    @MockBean
    private MemberRepository memberRepository;


    @AfterEach
    public void cleanup(){
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("indMemberByNickname() 메서드를 호출하면 처음을 제외한 나머지는 Cache가 인터셉트한다.")
    void findMemberByNicknameUsedCache() throws IOException {
        // given
        Member mockMember = getMember("홍길동", Gender.MALE, getLocalDate("2023-05-15"));

        given(memberRepository.findMemberByNickname(anyString()))
                .willReturn(Optional.of(mockMember));

        // when
        IntStream.range(0, 10).forEach((i) -> memberService.findMemberByNickname("홍길동"));

        // then
        verify(memberRepository, times((1))).findMemberByNickname("홍길동");
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

    private List<MultipartFile> getMultipartFiles() {
        String path1 = "male.png";
        String contentType1 = "image/png";
        String path2 = "female.png";
        String contentType2 = "image/png";

        return List.of(new MockMultipartFile("male", path1, contentType1, "male".getBytes())
                , new MockMultipartFile("female", path2, contentType2, "female".getBytes()));
    }

    private LocalDate getLocalDate(String firstDate) {
        return LocalDate.parse(firstDate, DateTimeFormatter.ISO_DATE);
    }
}
