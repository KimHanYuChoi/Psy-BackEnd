package com.sentinels.psychology.Member.dto;

import com.sentinels.psychology.Member.domain.Role;
import lombok.Builder;

public class MemberDto {
    String name;
    String password;
    Role role;

    @Builder
    private MemberDto(String name, String password, Role role) {
        this.name = name;
        this.password = password;
        this.role = role;
    }
}
