package com.ege.wooda.global.security.oauth.common.mapper;

import java.util.Map;

import com.ege.wooda.global.security.oauth.model.OAuth2Request;

public interface AttributeMapper {
    OAuth2Request mapToDto(Map<String, Object> attributes);
}
