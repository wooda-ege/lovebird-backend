package com.ege.wooda.global.security.oauth.common.mapper;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.ege.wooda.global.security.oauth.model.OAuth2Request;
import com.ege.wooda.global.security.oauth.model.enums.SocialProvider;

@Component
public class NaverAttributeMapper implements AttributeMapper {

    @Override
    public OAuth2Request mapToDto(Map<String, Object> attributes) {
        String accountId = (attributes.get("id")).toString();
        String name = (String) attributes.get("name");
        String email = (String) ((Map<String, Object>) attributes.get("response")).get("email");

        return new OAuth2Request(accountId, name, email, SocialProvider.NAVER);
    }
}
