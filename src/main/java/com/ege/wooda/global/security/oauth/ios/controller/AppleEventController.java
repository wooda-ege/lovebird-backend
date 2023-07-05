package com.ege.wooda.global.security.oauth.ios.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ege.wooda.Application;
import com.ege.wooda.global.security.oauth.ios.dto.AppleEventResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/management/apple")
public class AppleEventController {
    private final static Logger logger = LoggerFactory.getLogger(Application.class);
    @PostMapping
    public void loggingAppleEvent(@RequestBody AppleEventResponse appleEventResponse) {
        logger.trace("[Apple Event Log] PAYLOAD : " + appleEventResponse.payload());
    }

}
