package com.ege.wooda.global.security.oauth.common.mapper;

import java.util.EnumMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ege.wooda.global.security.oauth.model.enums.SocialProvider;

@Component
public class AttributeMapperFactory {
    private final Map<SocialProvider, AttributeMapper> mapperMap;
    private final GoogleAttributeMapper googleAttributeMapper;
    private final KakaoAttributeMapper kakaoAttributeMapper;

    public AttributeMapperFactory(GoogleAttributeMapper googleAttributeMapper, KakaoAttributeMapper kakaoAttributeMapper) {
        this.mapperMap = new EnumMap<>(SocialProvider.class);
        this.googleAttributeMapper = googleAttributeMapper;
        this.kakaoAttributeMapper = kakaoAttributeMapper;

        initialize();
    }

    private void initialize() {
        mapperMap.put(SocialProvider.GOOGLE, googleAttributeMapper);
        mapperMap.put(SocialProvider.KAKAO, kakaoAttributeMapper);
    }

    public AttributeMapper getAttributeMapper(SocialProvider socialProvider) {
        return mapperMap.get(socialProvider);
    }
}
