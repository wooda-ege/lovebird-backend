package com.ege.wooda.domain.member.repository;

import com.ege.wooda.domain.member.domain.Gender;
import com.ege.wooda.domain.member.domain.Member;
import com.ege.wooda.global.config.jpa.JpaConfig;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Import(JpaConfig.class)
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @AfterEach
    void cleanUp() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("새로운 Member 생성한다")
    public void save() {
        // given
        Member member = getMember("홍길동", Gender.MALE, getLocalDate("2023-05-09"));
        memberRepository.save(member);

        // when
        Member existMember = memberRepository.findById(member.getId())
                                             .orElseThrow(EntityNotFoundException::new);

        // then
        assertEquals(member.getId(), existMember.getId());
        assertEquals(member.getGender(), existMember.getGender());
        assertEquals(member.getFirstDate(), existMember.getFirstDate());
        assertEquals(member.getNickname(), existMember.getNickname());
    }

    @Test
    @DisplayName("Nickname을 통해 Member를 찾는 데 성공한다.")
    public void findMemberByNicknameSuccess() {
        // given
        List<Member> memberList = getMemberList();

        memberRepository.saveAll(memberList);

        // when
        Member secondMember = memberList.get(1);
        Member existMember = memberRepository.findMemberByNickname(secondMember.getNickname())
                                             .orElseThrow(EntityNotFoundException::new);

        //then
        assertEquals(secondMember.getId(), existMember.getId());
        assertEquals(secondMember.getGender(), existMember.getGender());
        assertEquals(secondMember.getFirstDate(), existMember.getFirstDate());
        assertEquals(secondMember.getNickname(), existMember.getNickname());
    }

    @Test
    @DisplayName("Nickname을 통해 Member를 찾는 데 실패한다.")
    public void findMemberByNicknameFail() {
        // given
        List<Member> memberList = getMemberList();

        memberRepository.saveAll(memberList);

        // when
        assertThrows(EntityNotFoundException.class,
                     () -> memberRepository.findMemberByNickname("Data")
                                           .orElseThrow(EntityNotFoundException::new));
    }

    private Member getMember(String nickname, Gender gender, LocalDate firstDate) {
        return Member.builder()
                     .uuid(UUID.randomUUID().toString())
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

    private LocalDate getLocalDate(String firstDate) {
        return LocalDate.parse(firstDate, DateTimeFormatter.ISO_DATE);
    }
}