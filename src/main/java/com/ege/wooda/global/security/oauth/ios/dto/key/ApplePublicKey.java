package com.ege.wooda.global.security.oauth.ios.dto.key;

public record ApplePublicKey(String kty,
                             String kid,
                             String alg,
                             String n,
                             String e) {
}
