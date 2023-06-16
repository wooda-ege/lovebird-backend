package com.ege.wooda.global.security.oauth.service;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.ege.wooda.domain.member.service.MemberService;
import com.ege.wooda.global.security.oauth.common.mapper.AttributeMapperFactory;
import com.ege.wooda.global.security.oauth.common.mapper.PrincipalUserMapper;
import com.ege.wooda.global.security.oauth.model.enums.SocialProvider;
import com.ege.wooda.global.security.oauth.model.OAuth2Request;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomOidcUserService extends OidcUserService {
    private final MemberService memberService;
    private final AttributeMapperFactory attributeMapperFactory;
    private final PrincipalUserMapper principalUserMapper;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        SocialProvider socialProvider = SocialProvider.valueOf(userRequest.getClientRegistration()
                                                                          .getClientName()
                                                                          .toUpperCase());
        OAuth2Request oAuth2Request = attributeMapperFactory.getAttributeMapper(socialProvider)
                                                            .mapToDto(oidcUser.getAttributes());

        return principalUserMapper.toPrincipalUser(memberService.saveIfNewMember(oAuth2Request));
    }
}
