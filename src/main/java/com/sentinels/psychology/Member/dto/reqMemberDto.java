package com.sentinels.psychology.Member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class reqMemberDto {
    String name;
    String password;

    @Builder
    private reqMemberDto(String name, String password) {
        this.name = name;
        this.password = password;
    }
}
