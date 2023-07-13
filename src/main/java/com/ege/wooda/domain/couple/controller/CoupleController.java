package com.ege.wooda.domain.couple.controller;

import com.ege.wooda.domain.couple.domain.Couple;
import com.ege.wooda.domain.couple.dto.response.CoupleResponseMessage;
import com.ege.wooda.domain.couple.exception.CodeExpiredException;
import com.ege.wooda.domain.couple.service.CoupleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ege.wooda.domain.member.service.MemberService;
import com.ege.wooda.domain.couple.dto.param.ConnectCoupleParam;
import com.ege.wooda.domain.couple.dto.request.CoupleLinkRequest;
import com.ege.wooda.domain.couple.dto.response.CoupleCodeResponse;
import com.ege.wooda.global.common.response.ApiResponse;
import com.ege.wooda.global.firebase.dto.NotificationRequest;
import com.ege.wooda.global.firebase.service.FCMService;
import com.google.firebase.messaging.FirebaseMessagingException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/{id}/couple")
public class CoupleController {
    private final MemberService memberService;
    private final CoupleService coupleService;
    private final FCMService fcmService;

    @PreAuthorize("#id == authentication.principal.id")
    @GetMapping("/code")
    public ResponseEntity<ApiResponse<CoupleCodeResponse>> generateCode(@PathVariable Long id) {
        Couple couple = coupleService.getCoupleCode(id);

        return ResponseEntity.ok(
                ApiResponse.createSuccessWithData(CoupleResponseMessage.GENERATE_COUPLE_CODE.getMessage(),
                                                  couple.toCoupleCodeResponse()));
    }

    @PreAuthorize("#id == authentication.principal.id")
    @PutMapping("/link")
    public ResponseEntity<ApiResponse<?>> linkCouple(@PathVariable Long id,
                                                     @RequestBody CoupleLinkRequest coupleLinkRequest)
            throws FirebaseMessagingException, CodeExpiredException {
        Long partnerId = coupleService.connectCouple(
                new ConnectCoupleParam(id, coupleLinkRequest.coupleCode()));
        NotificationRequest notificationRequest = NotificationRequest.builder()
                                                                     .deviceToken(
                                                                             memberService.findById(partnerId)
                                                                                          .getDeviceToken())
                                                                     .title("커플 연동 코드")
                                                                     .body(CoupleResponseMessage.LINK_SUCCESS.getMessage())
                                                                     .build();
        fcmService.sendNotification(notificationRequest);

        return ResponseEntity.ok(ApiResponse.createSuccess(CoupleResponseMessage.LINK_SUCCESS.getMessage()));
    }
}
