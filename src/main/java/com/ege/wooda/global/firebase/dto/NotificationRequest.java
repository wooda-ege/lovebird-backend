package com.ege.wooda.global.firebase.dto;

import com.google.firebase.messaging.Notification;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

public record NotificationRequest(@NotBlank String deviceToken,
                                  String title,
                                  String body) {

    @Builder
    public NotificationRequest {}

    public Notification toNotification() {
        return Notification.builder()
                           .setTitle(title)
                           .setBody(body)
                           .build();
    }
}
