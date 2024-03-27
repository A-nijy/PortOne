package test.pay.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.pay.domain.Member;
import test.pay.domain.repository.MemberRepository;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;


    // 자동 회원가입
    public Member autoJoin() {

        log.info("MemberService -> autoJoin() 호출 -> 회원 객체 생성 예정 (회원 가입)");
        Member member = Member.builder()
                .username(UUID.randomUUID().toString())
                .email("example@example.com")
                .address("대전광역시 서구 둔산동")
                .build();

        log.info("회원 객체 생성 완료 (회원 가입) -> 회원 객체 DB에 저장 예정");
        memberRepository.save(member);

        log.info("회원 객체 DB에 저장 완료 -> 해당 회원 객체 반환");
        return member;
    }
}
