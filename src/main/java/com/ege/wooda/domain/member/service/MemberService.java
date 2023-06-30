package com.ege.wooda.domain.member.service;

import com.ege.wooda.domain.member.domain.Member;

import com.ege.wooda.domain.member.domain.Oauth2Entity;
import com.ege.wooda.domain.member.repository.MemberRepository;
import com.ege.wooda.global.security.oauth.model.OAuth2Request;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public Member saveIfNewMember(OAuth2Request oAuth2Request) {
        return memberRepository.findByOauth2EntityAccountId(oAuth2Request.accountId()).orElseGet(
                () -> save(oAuth2Request.toOauth2Entity()));
    }

    @Transactional
    public void registerDeviceToken(Long id, String deviceToken) {
        Member member = findById(id);
        member.registerDeviceToken(deviceToken);

    }

    public Member save(Oauth2Entity oauth2Entity) {
        return memberRepository.save(new Member(oauth2Entity));
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "member", key = "#id", value = "member")
    public Member findById(Long id) {
        return memberRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
