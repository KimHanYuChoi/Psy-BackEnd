package com.sentinels.psychology.Member.controller;


import com.sentinels.psychology.Member.dto.MemberDto;
import com.sentinels.psychology.Member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberDto> getMemberById(@PathVariable Long memberId) {
        MemberDto memberDto = memberService.findMemberById(memberId);
        return ResponseEntity.status(HttpStatus.OK).body(memberDto);
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<String> deleteMemberById(@PathVariable Long memberId) {
        memberService.deleteMember(memberId);
        return ResponseEntity.status(HttpStatus.OK).body("Address deleted successfully");
    }
}
