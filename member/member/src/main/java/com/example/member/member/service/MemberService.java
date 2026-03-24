package com.example.member.member.service;

import com.example.member.member.domain.Member;
import com.example.member.member.domain.Role;
import com.example.member.member.dto.LoginDto;
import com.example.member.member.dto.MemberSaveReqDto;
import com.example.member.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

// CommandLineRunner 구현하면 스프링 빈으로 등록되는 시점에 run 메서드 실행
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Long save(MemberSaveReqDto memberSaveReqDto) {
        Optional<Member> byEmail = memberRepository.findByEmail(memberSaveReqDto.getEmail());

        if (byEmail.isPresent()) {
            throw new IllegalArgumentException("기존에 존재하는 회원");
        }
        String password = passwordEncoder.encode(memberSaveReqDto.getPassword());
        Member member = memberRepository.save(memberSaveReqDto.toEntity(password));

        return member.getId();
    }

    public Member login(LoginDto dto) {
        boolean check = true;
        Optional<Member> byEmail = memberRepository.findByEmail(dto.getEmail());

        if (!byEmail.isPresent()) {
            check = false;
        }

        if (!passwordEncoder.matches(dto.getPassword(), byEmail.get().getPassword())) {
            check = false;
        }

        if (!check) {
            throw new IllegalArgumentException("email 또는 비밀번호가 일치하지 않습니다");
        }

        return byEmail.get();
    }

}
