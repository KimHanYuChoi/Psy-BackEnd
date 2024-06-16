package com.sentinels.psychology.Member.service;

import com.sentinels.psychology.Member.domain.Member;
import com.sentinels.psychology.Member.dto.MemberDto;
import com.sentinels.psychology.Member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public MemberDto findMemberById(Long memberId) {
        Optional<Member> findMember = memberRepository.findById(memberId);
        Member member = findMember.orElseThrow(IllegalArgumentException::new);
        MemberDto memberDto = MemberDto.builder().
                name(member.getName()).
                password(member.getPassword()).
                role(member.getRole()).
                build();

        return memberDto;
    }

    @Transactional
    public void deleteMember(Long memberId) {
        try {
            memberRepository.deleteById(memberId);
        } catch (Exception e) {
            System.err.println("Member deletion failed: " + e.getMessage());
            throw e;
        }
    }
}
