package com.example.spring;

import com.example.spring.aop.TimeTraceAop;
import com.example.spring.repository.*;
import com.example.spring.service.MemberService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * componet scan을 사용하지 않고 직접 Bean 에 저장하는 방법
 *
 * Autowired 는 spring container 안에 등록된 것만 동작함
 */

@Configuration
public class SpringConfig {

//    private final DataSource dataSource;
//    private final EntityManager em;

    private final MemberRepository memberRepository;

    @Autowired  // 생성자가 하나인 경우 Autowired 생략 가능
    public SpringConfig(MemberRepository memberRepository) {
//        this.dataSource = dataSource;
//        this.em = entityManager;
        this.memberRepository = memberRepository;
    }

    @Bean
    public MemberService memberService() {
        return new MemberService(memberRepository);
    }

//    @Bean
//    public MemberRepository memberRepository() {
//        return new MemoryMemberRepository();
//        return new JdbcMemberRepository(dataSource);
//        return new JdbcTemplateMemberRepository(dataSource);
//        return new JpaMemberRepository(em);
//    }
}
