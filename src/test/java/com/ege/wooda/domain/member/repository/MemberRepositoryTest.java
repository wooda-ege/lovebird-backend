package com.ege.wooda.domain.member.repository;

import com.ege.wooda.domain.member.domain.Member;
import com.ege.wooda.domain.member.domain.Oauth2Entity;
import com.ege.wooda.global.config.jpa.JpaConfig;
import com.ege.wooda.global.security.oauth.model.enums.SocialProvider;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Import(JpaConfig.class)
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void cleanUp() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("새로운 Member 생성한다")
    public void save() {
        // given
        Member member = getMember("abc12fea", SocialProvider.valueOf("GOOGLE"), "홍길동");
        memberRepository.save(member);

        // when
        Member saveMember = memberRepository.findById(member.getId())
                                             .orElseThrow(EntityNotFoundException::new);

        // then
        assertEquals(member, saveMember);
    }

    @Test
    @DisplayName("특정 id로 Member를 찾는 데 성공한다.")
    public void findByIdSuccess() {
        // given
        memberRepository.saveAll(getMemberList());
        Long findId = 2L;

        // when
        Member findMember = memberRepository.findById(findId).orElseThrow(EntityNotFoundException::new);

        //then
        assertEquals(findMember.getId(), findId);
    }

    @Test
    @DisplayName("특정 id로 Member를 찾는 데 실패한다.")
    public void findByIdFail() {
        // given
        memberRepository.saveAll(getMemberList());
        Long findId = Long.MAX_VALUE;

        // then
        assertThrows(EntityNotFoundException.class,
                     () -> memberRepository.findById(findId).orElseThrow(EntityNotFoundException::new));
    }

    @Test
    @DisplayName("accountId를 통해 Member를 찾는 데 성공한다.")
    public void findByOauth2EntityAccountIdSuccess() {
        // given
        memberRepository.saveAll(getMemberList());
        String accountId = "abc12fea";

        // when
        Member findMember = memberRepository.findByOauth2EntityAccountId(accountId).orElseThrow(
                EntityNotFoundException::new);

        //then
        assertEquals(findMember.getOauth2Entity().getAccountId(), accountId);
    }

    @Test
    @DisplayName("accountId를 통해 Member를 찾는 데 실패한다.")
    public void findByOauth2EntityAccountIdFail() {
        // given
        memberRepository.saveAll(getMemberList());
        String accountId = "bhq21kv2oq4";

        //then
        assertThrows(EntityNotFoundException.class,
                     () -> memberRepository.findByOauth2EntityAccountId(accountId)
                                           .orElseThrow(EntityNotFoundException::new));
    }

    private Member getMember(String accountId, SocialProvider socialProvider, String username) {
        return Member.builder()
                     .oauth2Entity(getOauth2Entity(accountId,
                                                   socialProvider,
                                                   username))
                     .build();
    }

    private Oauth2Entity getOauth2Entity(String accountId, SocialProvider socialProvider, String username) {
        return Oauth2Entity.builder()
                           .accountId(accountId)
                           .socialProvider(socialProvider)
                           .email("test@" + socialProvider.toString().toLowerCase() + "." + "com")
                           .username(username)
                           .build();
    }

    private List<Member> getMemberList() {
        return List.of(getMember("abc12fea", SocialProvider.valueOf("GOOGLE"), "홍길동")
                , getMember("ebu2ap3o1", SocialProvider.valueOf("KAKAO"), "청길동")
                , getMember("vqu41ob9", SocialProvider.valueOf("APPLE"), "녹길동"));
    }
}