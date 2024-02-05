package com.example.jpashop.repository;

import com.example.jpashop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // JPQL 자동 생성 쿼리
    // JPQL: select m from Member m where m.name = :name
    List<Member> findByName(String name);
}
