package com.ege.wooda.global.security.oauth.ios.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.ege.wooda.global.security.oauth.ios.dto.key.ApplePublicKeyResponse;

@FeignClient(name = "appleAuthClient", url = "${apple.auth.public-key-url}")
public interface AppleAuthClient {
    @GetMapping
    ApplePublicKeyResponse getAppleAuthPublicKey();
}
