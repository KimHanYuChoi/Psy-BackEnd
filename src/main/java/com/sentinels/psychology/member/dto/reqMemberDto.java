package com.sentinels.psychology.member.dto;

import com.sentinels.psychology.member.domain.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class reqMemberDto {
    String username;
    String password;
    Role role;

    @Builder
    private reqMemberDto(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
