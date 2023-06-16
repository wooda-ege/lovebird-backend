package com.ege.wooda.global.security.oauth.common.mapper;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.ege.wooda.global.security.oauth.model.OAuth2Request;
import com.ege.wooda.global.security.oauth.model.enums.SocialProvider;

@Component
public class GoogleAttributeMapper implements AttributeMapper {
    @Override
    public OAuth2Request mapToDto(Map<String, Object> attributes) {
        String accountId = (String) attributes.get("sub");
        String name = (String) attributes.get("name");
        String email = (String) attributes.get("email");

        return new OAuth2Request(accountId, name, email, SocialProvider.GOOGLE);
    }
}
