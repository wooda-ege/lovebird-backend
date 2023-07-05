package com.ege.wooda.global.security.oauth.ios.dto.key;

import java.util.List;
import java.util.Optional;

import javax.naming.AuthenticationException;

import org.springframework.web.client.HttpClientErrorException.Unauthorized;

public record ApplePublicKeyResponse(List<ApplePublicKey> keys) {

    public ApplePublicKey getMatchedKey(String kid, String alg) throws AuthenticationException {
        return keys.stream()
                   .filter(key -> key.kid().equals(kid) && key.alg().equals(alg))
                   .findAny()
                   .orElseThrow(AuthenticationException::new);
    }
}
