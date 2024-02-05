package com.example.jpashop.service;

import com.example.jpashop.domain.Member;
import com.example.jpashop.repository.MemberRepositoryOld;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepositoryOld memberRepository;

    @Test
    void join() {
        // given
        Member member = new Member();
        member.setName("kim");

        // when
        Long memberId = memberService.join(member);
        Member findMember = memberService.findOne(memberId);

        // then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
    }

    @Test
    void validateMember() {
        // given
        Member member1 = new Member();
        member1.setName("kim");
        Member member2 = new Member();
        member2.setName("kim");

        // when
        memberService.join(member1);

        // then
        Assertions.assertThatThrownBy(() -> memberService.join(member2))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 존재하는 회원입니다.");
    }
}