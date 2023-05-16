package com.ege.wooda.domain.member.controller;

import com.ege.wooda.domain.member.dto.request.MemberCreateRequest;
import com.ege.wooda.domain.member.dto.request.MemberUpdateRequest;
import com.ege.wooda.domain.member.service.MemberService;
import com.ege.wooda.global.common.response.DefaultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequestMapping("/members")
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("")
    public DefaultResponse<Object> add(@RequestPart(value = "images") List<MultipartFile> images,
                                       @RequestPart(value = "memberCreateRequest") MemberCreateRequest memberCreateRequest) throws IOException {
        return DefaultResponse.builder()
                .statusCode(201)
                .responseMessage("SUCCESS")
                .data(memberService.save(images, memberCreateRequest))
                .build();
    }

    @GetMapping("/{nickname}")
    public DefaultResponse<Object> details(@PathVariable String nickname) {
        return DefaultResponse.builder()
                .statusCode(200)
                .responseMessage("SUCCESS")
                .data(memberService.findMemberByNickname(nickname).toMemberDetailResponse())
                .build();
    }

    @PutMapping("/{nickname}")
    public DefaultResponse<Object> modify(@PathVariable String nickname,
                                          @RequestPart(value = "images") List<MultipartFile> images,
                                          @RequestPart(value = "memberUpdateRequest") MemberUpdateRequest memberUpdateRequest) throws IOException {
        return DefaultResponse.builder()
                .statusCode(200)
                .responseMessage("SUCCESS")
                .data(memberService.update(nickname, images, memberUpdateRequest))
                .build();
    }

    @DeleteMapping("/{nickname}")
    public DefaultResponse<Object> remove(@PathVariable String nickname){
        memberService.delete(nickname);
        return DefaultResponse.builder()
                .statusCode(200)
                .responseMessage("SUCCESS")
                .build();
    }
}
