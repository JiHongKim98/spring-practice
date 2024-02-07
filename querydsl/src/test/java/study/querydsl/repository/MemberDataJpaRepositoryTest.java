package study.querydsl.repository;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import java.util.List;

@Slf4j
@Transactional
@SpringBootTest
class MemberDataJpaRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberDataJpaRepository memberDataJpaRepository;

    @BeforeEach
    void before() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);

        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    /**
     * JPA DATA 를 이용한 findByUsername 테스트
     */
    @Test
    void jpaDataFindByUsername() {
        Member member = new Member("member1", 10);
        memberDataJpaRepository.save(member);

        Member findMember = memberDataJpaRepository.findById(member.getId()).get();

        List<Member> result = memberDataJpaRepository.findByUsername("member1");
        Assertions.assertThat(result).contains(findMember);
        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    /**
     * MemberDataJpaRepositoryCustom 을 상속 받은 MemberDataJpaRepository
     * 즉, Custom Data JPA 의 search 메소드 테스트
     */
    @Test
    void CustomJpaDataSearch() {
        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setAgeGoe(35);
        condition.setAgeLoe(40);
        condition.setTeamName("teamB");

        List<MemberTeamDto> result = memberDataJpaRepository.search(condition);

        Assertions.assertThat(result).extracting("username").containsExactly("member4");
    }

    /**
     * QueryDSL 과 Data JPA 를 사용한 페이징 처리 테스트 (simple - fetchResults)
     * searchPageSimple 메소드 테스트
     * ! 주의 할 점은 `fetchResults`은 현재 지원 중단.
     * Ref. https://velog.io/@nestour95/QueryDsl-fetchResults가-deprecated-된-이유
     */
    @Test
    void searchPageSimple() {
        MemberSearchCondition condition = new MemberSearchCondition();
        // 0번 인덱스 부터 한 페이지에 3개의 데이터를 가져옴
        PageRequest pageRequest = PageRequest.of(0, 3);

        Page<MemberTeamDto> result = memberDataJpaRepository.searchPageSimple(condition, pageRequest);

        Assertions.assertThat(result.getSize()).isEqualTo(3);
        Assertions.assertThat(result.getContent()).extracting("username")
                .containsExactly("member1", "member2", "member3");
    }

    /**
     * QueryDSL 과 Data JPA 를 사용한 페이징 처리 테스트 (simple - fetchResults)
     * searchPageSimple 메소드 테스트
     * ! 주의 할 점은 `fetchResults`은 현재 지원 중단.
     * Ref.1 https://velog.io/@nestour95/QueryDsl-fetchResults가-deprecated-된-이유
     * Ref.2 https://www.inflearn.com/questions/806452/querydsl-5-0-0-기준으로-강의-내용을-정리했는데-올바르게-이해한-것일까요
     */
    @Test
    void searchPageComplex() {

        MemberSearchCondition condition = new MemberSearchCondition();
        // 0번 인덱스 부터 한 페이지에 3개의 데이터를 가져옴
        PageRequest pageRequest = PageRequest.of(0, 3);

        Page<MemberTeamDto> result = memberDataJpaRepository.searchPageComplex(condition, pageRequest);

        Assertions.assertThat(result.getSize()).isEqualTo(3);
        Assertions.assertThat(result.getContent()).extracting("username")
                .containsExactly("member1", "member2", "member3");
    }

    /**
     * Data JPA 가 제공하는 `QuerydslPredicateExecutor` 를 통해
     * QueryDSL 을 사용하는 방법
     * 하지만, left join 이 불가능하고, 클라이언트가 QueryDSL 에 의존해야 한다.
     * 즉, 복잡한 실무 환경에서 사용하기에 한계가 명확해 권장하지 않는다.
     */
    @Test
    void querydslPredicateExecutorTest() {
        QMember member = QMember.member;
        Iterable<Member> result = memberDataJpaRepository.findAll(
                member.age.between(10, 40)
                        .and(
                                member.username.eq("member1")
                        )
        );

        for (Member findMember : result) {
            System.out.println("findMember = " + findMember);
        }
    }

}