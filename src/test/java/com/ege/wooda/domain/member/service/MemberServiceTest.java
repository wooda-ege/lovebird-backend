package com.ege.wooda.domain.member.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.ege.wooda.domain.member.domain.Member;
import com.ege.wooda.domain.member.domain.Oauth2Entity;
import com.ege.wooda.domain.member.repository.MemberRepository;
import com.ege.wooda.global.security.oauth.model.OAuth2Request;
import com.ege.wooda.global.security.oauth.model.enums.SocialProvider;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @AfterEach
    public void cleanup() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("Member를 저장한다.")
    void save() {
        // given
        Oauth2Entity oauth2Entity = getOauth2Entity("qv1go6fw", SocialProvider.valueOf("GOOGLE"), "홍길동");
        Member mockMember = getMember(oauth2Entity);

        given(memberRepository.save(any()))
                .willReturn(mockMember);

        // when
        Member saveMember = memberService.save(oauth2Entity);

        // then
        assertEquals(mockMember, saveMember);
    }

    @Test
    @DisplayName("만약 존재하지 않는 Member일 때 저장한다.")
    void saveIfNewMemberSuccess() {
        // given
        Oauth2Entity oauth2Entity = getOauth2Entity("qv1go6fw", SocialProvider.valueOf("GOOGLE"), "홍길동");
        Member mockMember = getMember(oauth2Entity);

        given(memberRepository.findByOauth2EntityAccountId(anyString()))
                .willReturn(Optional.empty());
        given(memberRepository.save(any()))
                .willReturn(mockMember);

        // when
        OAuth2Request oAuth2Request = OAuth2Request.builder()
                                                   .accountId("bq2guq4if")
                                                   .name("홍길동")
                                                   .email("test@gmail.com")
                                                   .socialProvider(SocialProvider.valueOf("GOOGLE"))
                                                   .build();

        Member saveMember = memberService.saveIfNewMember(oAuth2Request);

        // then
        assertEquals(mockMember, saveMember);
    }

    @Test
    @DisplayName("만약 존재하는 경우 Member를 기존 데이터를 리턴한다.")
    void saveIfNewMemberFail() {
        // given
        Oauth2Entity oauth2Entity = getOauth2Entity("qv1go6fw", SocialProvider.valueOf("GOOGLE"), "홍길동");
        Member mockMember = getMember(oauth2Entity);

        given(memberRepository.findByOauth2EntityAccountId(anyString()))
                .willReturn(Optional.of(mockMember));

        // when
        OAuth2Request oAuth2Request = OAuth2Request.builder()
                                                   .accountId("bq2guq4if")
                                                   .name("홍길동")
                                                   .email("test@gmail.com")
                                                   .socialProvider(SocialProvider.valueOf("GOOGLE"))
                                                   .build();

        Member existMember = memberService.saveIfNewMember(oAuth2Request);

        // then
        assertEquals(mockMember, existMember);
    }

    @Test
    @DisplayName("해당 id의 Member를 리턴한다.")
    void findById() {
        // given
        Long mockId = 1L;
        Member mockMember = getMember(getOauth2Entity("qv1go6fw", SocialProvider.valueOf("GOOGLE"), "홍길동"));

        ReflectionTestUtils.setField(mockMember, "id", mockId);
        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(mockMember));

        // when
        Member findMember = memberService.findById(mockId);

        //then
        assertEquals(mockMember, findMember);
    }

    private Member getMember(Oauth2Entity oauth2Entity) {
        return Member.builder()
                     .oauth2Entity(oauth2Entity)
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
}
