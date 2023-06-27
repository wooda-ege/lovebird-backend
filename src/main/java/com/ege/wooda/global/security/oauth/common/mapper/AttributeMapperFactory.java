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
    private final NaverAttributeMapper naverAttributeMapper;

    public AttributeMapperFactory(GoogleAttributeMapper googleAttributeMapper, KakaoAttributeMapper kakaoAttributeMapper, NaverAttributeMapper naverAttributeMapper) {
        this.mapperMap = new EnumMap<>(SocialProvider.class);
        this.googleAttributeMapper = googleAttributeMapper;
        this.kakaoAttributeMapper = kakaoAttributeMapper;
        this.naverAttributeMapper = naverAttributeMapper;

        initialize();
    }

    private void initialize() {
        mapperMap.put(SocialProvider.GOOGLE, googleAttributeMapper);
        mapperMap.put(SocialProvider.KAKAO, kakaoAttributeMapper);
        mapperMap.put(SocialProvider.NAVER, naverAttributeMapper);
    }

    public AttributeMapper getAttributeMapper(SocialProvider socialProvider) {
        return mapperMap.get(socialProvider);
    }
}
