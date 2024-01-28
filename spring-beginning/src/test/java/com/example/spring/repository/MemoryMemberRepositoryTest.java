package com.example.spring.repository;

import com.example.spring.domain.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;  // assertThat 을 한번에 사용 가능해짐

public class MemoryMemberRepositoryTest {

    MemoryMemberRepository repository = new MemoryMemberRepository();

    @AfterEach  // AfterEach : 메서드가 끝날 때마다 call back으로 동작하는 메소드
    public void afterEach() {
        repository.clearStore();
    }


    @Test
    public void save() {
        Member member = new Member();
        member.setUsername("kimjihong");

        repository.save(member);

        // get으로 바로 꺼내는건 좋지 않지만 일단 테스트 코드
        Member result = repository.findById(member.getId()).get();
        Assertions.assertEquals(member, result);
        assertThat(member).isEqualTo(result);
    }

    @Test
    public void findByid() {
        Member member1 = new Member();
        member1.setUsername("kimjihong-1");
        repository.save(member1);

        Member member2 = new Member();
        member2.setUsername("kimjihong-2");
        repository.save(member2);

        Member result = repository.findByUsername("kimjihong-1").get();

        assertThat(member1).isEqualTo(result);
    }

    @Test
    public void findByUsername() {
        Member member1 = new Member();
        member1.setUsername("kimjihong-1");
        repository.save(member1);

        Member member2 = new Member();
        member2.setUsername("kimjihong-2");
        repository.save(member2);

        Member result = repository.findByUsername("kimjihong-1").get();
        assertThat(member1).isEqualTo(result);
    }

    @Test
    public void findAll() {
        Member member1 = new Member();
        member1.setUsername("kimjihong-1");
        repository.save(member1);

        Member member2 = new Member();
        member2.setUsername("kimjihong-2");
        repository.save(member2);

        List<Member> result = repository.findAll();

        assertThat(result.size()).isEqualTo(2);
    }
}
