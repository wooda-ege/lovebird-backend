package com.ege.wooda.global.security.oauth.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.ege.wooda.domain.member.service.MemberService;
import com.ege.wooda.global.security.oauth.common.mapper.AttributeMapperFactory;
import com.ege.wooda.global.security.oauth.common.mapper.PrincipalUserMapper;
import com.ege.wooda.global.security.oauth.model.enums.SocialProvider;
import com.ege.wooda.global.security.oauth.model.OAuth2Request;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberService memberService;
    private final AttributeMapperFactory attributeMapperFactory;
    private final PrincipalUserMapper principalUserMapper;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        SocialProvider socialProvider = SocialProvider.valueOf(userRequest.getClientRegistration()
                                                                          .getClientName()
                                                                          .toUpperCase());
        OAuth2Request oAuth2Request = attributeMapperFactory.getAttributeMapper(socialProvider)
                                                            .mapToDto(oAuth2User.getAttributes());

        return principalUserMapper.toPrincipalUser(memberService.saveIfNewMember(oAuth2Request));
    }
}
