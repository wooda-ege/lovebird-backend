package com.ege.wooda.global.firebase.service;

import org.springframework.stereotype.Service;

import com.ege.wooda.global.firebase.dto.NotificationRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FCMService {
    private final FirebaseMessaging firebaseMessaging;

    public void sendNotification(NotificationRequest notificationRequest) throws FirebaseMessagingException {
        Message message = Message.builder()
                                 .setToken(notificationRequest.deviceToken())
                                 .setNotification(notificationRequest.toNotification())
                                 .build();

        firebaseMessaging.send(message);
    }
}
