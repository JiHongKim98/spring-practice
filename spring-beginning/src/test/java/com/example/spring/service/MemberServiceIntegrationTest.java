package com.example.spring.service;

import com.example.spring.domain.Member;
import com.example.spring.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
class MemberServiceIntegrationTest {

    @Autowired MemberService memberService;

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
    }
}