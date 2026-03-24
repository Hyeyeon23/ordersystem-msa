package com.example.member.member.service;

import com.example.member.member.domain.Member;
import com.example.member.member.domain.Role;
import com.example.member.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// CommandLineRunner 구현하면 스프링 빈으로 등록되는 시점에 run 메서드 실행
@RequiredArgsConstructor
@Component
public class InitialDataLoader implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) throws Exception {
        if (memberRepository.findByEmail("admin@naver.com").isPresent()) return;

        Member member = Member.builder()
                .name("admin")
                .email("admin@naver.com")
                .password(passwordEncoder.encode("1234"))
                .role(Role.ADMIN)
                .build();

        memberRepository.save(member);
    }
}
