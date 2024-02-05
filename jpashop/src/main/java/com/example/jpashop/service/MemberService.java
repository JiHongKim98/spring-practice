package com.example.jpashop.service;

import com.example.jpashop.domain.Member;
import com.example.jpashop.repository.MemberRepository;
import com.example.jpashop.repository.MemberRepositoryOld;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // readOnly 를 true 를 넣으면 데이터베이스가 최적화 될 수 있다.
@RequiredArgsConstructor
public class MemberService {

//    private final MemberRepositoryOld memberRepository;
    private final MemberRepository memberRepository;

    // 회원 가입
    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    // 중복 회원 검증
    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());

        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // 회원 한건만 조회
    public Member findOne(Long memberId) {
        return memberRepository.findById(memberId).get();
    }

    // 회원 수정
    @Transactional
    public void update(Long memberId, String name) {
        Member findMember = memberRepository.findById(memberId).get();
        findMember.setName(name);
    }
}
