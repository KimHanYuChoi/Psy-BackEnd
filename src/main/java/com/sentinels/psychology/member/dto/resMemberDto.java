package com.sentinels.psychology.member.dto;

import com.sentinels.psychology.member.domain.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
public class resMemberDto {
    String username;
    String password;
    Role role;

    @Builder
    private resMemberDto(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
