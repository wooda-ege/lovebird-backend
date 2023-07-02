package com.ege.wooda.global.security.oauth.common.mapper;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.ege.wooda.global.security.oauth.model.OAuth2Request;
import com.ege.wooda.global.security.oauth.model.enums.SocialProvider;

@Component
public class KakaoAttributeMapper implements AttributeMapper{

    @Override
    public OAuth2Request mapToDto(Map<String, Object> attributes) {
        String accountId = (attributes.get("sub")).toString();
        String name = (attributes.get("nickname")).toString();
        String email = (attributes.get("email")).toString();

        if(email == null || email.isEmpty()) {
            email = null;
        }

        return new OAuth2Request(accountId, name, email, SocialProvider.KAKAO);
    }
}
