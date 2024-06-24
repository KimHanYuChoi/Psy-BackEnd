package com.sentinels.psychology.member.service;

import com.sentinels.psychology.member.domain.Member;
import com.sentinels.psychology.member.dto.CustomMemberDetails;
import com.sentinels.psychology.member.dto.reqMemberDto;
import com.sentinels.psychology.member.dto.resMemberDto;
import com.sentinels.psychology.member.exception.MemberAlreadyExistsException;
import com.sentinels.psychology.member.exception.MemberNotFoundException;
import com.sentinels.psychology.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws MemberNotFoundException {

        Member memberEntity = memberRepository.findByUsername(username);

//        LoginFilter에서 BadCredentialsException 으로 래핑되어 출력
        if (memberEntity == null) {
            throw new MemberNotFoundException("");
        }

        return new CustomMemberDetails(memberEntity);
    }

    @Transactional
    public resMemberDto findMemberById(Long memberId) {
        Optional<Member> findMember = memberRepository.findById(memberId);
        Member member = findMember.orElseThrow(() ->
                new MemberNotFoundException("존재하지 않는 계정입니다."));

        return createResMemberDto(member);
    }

    @Transactional
    public void deleteMember(Long memberId) {
        try {
            memberRepository.deleteById(memberId);
        } catch (Exception e) {
            throw new MemberAlreadyExistsException("존재하지 않는 계정입니다.");
        }
    }

    @Transactional
    public resMemberDto join(reqMemberDto dto) {

        Boolean isExist = memberRepository.existsByUsername(dto.getUsername());

        if (isExist) {
            throw new MemberAlreadyExistsException(dto.getUsername() + " 이미 존재하는 이름입니다.");
        }

        Member memberEntity = createMemberEntity(dto);

        Member member = memberRepository.save(memberEntity);

        return createResMemberDto(member);
    }

    private resMemberDto createResMemberDto(Member member) {

        resMemberDto dto = resMemberDto.builder().
                username(member.getUsername()).
                password(member.getPassword()).
                role(member.getRole()).
                build();

        return dto;
    }

    private Member createMemberEntity(reqMemberDto dto) {
        Member member = Member.builder()
                .username(dto.getUsername())
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .role(dto.getRole())
                .build();

        return member;
    }
}
