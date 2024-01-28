package com.example.spring.service;

import com.example.spring.domain.Member;
import com.example.spring.repository.MemoryMemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemberServiceTest {

    MemberService memberService;
    MemoryMemberRepository memberRepository;

    // BeforeEach DI
    @BeforeEach
    public void beforeEach() {
        memberRepository = new MemoryMemberRepository();
        memberService = new MemberService(memberRepository);
    }

    @AfterEach
    public void afterEach() {
        memberRepository.clearStore();
    }

    @Test
    void join() {
        // given
        Member member = new Member();
        member.setUsername("kimjihong");

        // when
        Long saveId = memberService.join(member);

        // then
        Member findMember = memberService.findOne(saveId).get();
        assertThat(member.getUsername()).isEqualTo(findMember.getUsername());
    }

    @Test
    public void validateDuplicationMember() {
        // given
        Member member1 = new Member();
        member1.setUsername("kimjihong");

        Member member2 = new Member();
        member2.setUsername("kimjihong");

        // when
        memberService.join(member1);
        IllegalStateException e = Assertions.assertThrows(
                IllegalStateException.class, () -> memberService.join(member2)
        );

        assertThat(e.getMessage()).isEqualTo("already exist username");

/*
        try {
            memberService.join(member2);
            fail("Exception");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).isEqualTo("already exist username");
        }
*/

        // then
    }

    @Test
    void findMembers() {
    }

    @Test
    void findOne() {
    }
}