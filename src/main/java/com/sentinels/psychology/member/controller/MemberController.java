package com.sentinels.psychology.member.controller;

import com.sentinels.psychology.member.dto.reqMemberDto;
import com.sentinels.psychology.member.dto.resMemberDto;
import com.sentinels.psychology.member.exception.MemberAlreadyExistsException;
import com.sentinels.psychology.member.exception.MemberNotFoundException;
import com.sentinels.psychology.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody reqMemberDto dto) {
        try{
            resMemberDto resMemberDto = memberService.join(dto);
            return ResponseEntity.status(HttpStatus.OK).body(resMemberDto.toString());
        } catch (MemberAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());  // 409 Conflict
        }
    }

//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestBody reqMemberDto dto) {
//        try{
//            resMemberDto resMemberDto = memberService.login(dto);
//            return ResponseEntity.status(HttpStatus.OK).body(resMemberDto.toString());
//        } catch (MemberAlreadyExistsException e) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());  // 409 Conflict
//        }
//    }

    @GetMapping("/{memberId}")
    public ResponseEntity<resMemberDto> getMemberById(@PathVariable Long memberId) {

        try{
            resMemberDto dto = memberService.findMemberById(memberId);
            return ResponseEntity.status(HttpStatus.OK).body(dto);
        } catch (MemberNotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);  // 409 Conflict
        }
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<String> deleteMemberById(@PathVariable Long memberId) {

        try{
            memberService.deleteMember(memberId);
            return ResponseEntity.status(HttpStatus.OK).body("Address deleted successfully");
        } catch (MemberNotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());  // 409 Conflict
        }
    }
}
