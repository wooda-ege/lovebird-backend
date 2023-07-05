package com.ege.wooda.global.security.oauth.ios.util;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;

import javax.naming.AuthenticationException;

import org.springframework.stereotype.Component;

import com.ege.wooda.global.security.oauth.ios.dto.key.ApplePublicKey;
import com.ege.wooda.global.security.oauth.ios.dto.key.ApplePublicKeyResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ApplePublicKeyGenerator {
    public PublicKey generatePublicKey(Map<String, String> tokenHeaders,
                                       ApplePublicKeyResponse applePublicKeys)
            throws AuthenticationException, NoSuchAlgorithmException, InvalidKeySpecException {
        ApplePublicKey publicKey = applePublicKeys.getMatchedKey(tokenHeaders.get("kid"),
                                                                 tokenHeaders.get("alg"));

        return getPublicKey(publicKey);
    }

    private PublicKey getPublicKey(ApplePublicKey publicKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] nBytes = Base64.getUrlDecoder().decode(publicKey.n());
        byte[] eBytes = Base64.getUrlDecoder().decode(publicKey.e());

        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(new BigInteger(1, nBytes),
                                                              new BigInteger(1, eBytes));


        KeyFactory keyFactory = KeyFactory.getInstance(publicKey.kty());
        return keyFactory.generatePublic(publicKeySpec);
    }
}
