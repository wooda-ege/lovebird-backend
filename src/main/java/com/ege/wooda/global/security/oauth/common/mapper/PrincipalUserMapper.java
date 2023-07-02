package com.ege.wooda.global.security.oauth.common.mapper;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ege.wooda.domain.member.domain.Member;
import com.ege.wooda.global.security.oauth.model.PrincipalUser;

@Component
public class PrincipalUserMapper {
    public PrincipalUser toPrincipalUser(Member member) {
        Map<String, Object> attributes = new HashMap<>() {{
            put("id", member.getId());
        }};

        return new PrincipalUser(member, attributes, member.getRole());
    }
}
