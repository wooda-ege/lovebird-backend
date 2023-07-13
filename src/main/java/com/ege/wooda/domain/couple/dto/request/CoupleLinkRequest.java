package com.ege.wooda.domain.couple.dto.request;

import lombok.Builder;

public record CoupleLinkRequest(String coupleCode) {
    @Builder
    public CoupleLinkRequest{}
}
