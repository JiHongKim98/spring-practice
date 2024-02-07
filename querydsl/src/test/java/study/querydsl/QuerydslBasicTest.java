package study.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.QTeam;
import study.querydsl.entity.Team;

import java.util.List;

import static study.querydsl.entity.QMember.*;
import static study.querydsl.entity.QTeam.*;

@Slf4j
@Transactional
@SpringBootTest
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    /**
     * JPAQueryFactory 를 Field 로 빼서 사용하는 것을 권장.
     * EntityManager 와 JPAQueryFactory 모두 동시성 문제에 상관없이 설계 되어 있어
     * Field 로 빠져도 된다.
     */
    JPAQueryFactory queryFactory;

    @BeforeEach
    void before() {
        queryFactory = new JPAQueryFactory(em);

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

        // 초기화
        em.flush();
        em.clear();

        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        for (Member member: members) {
            log.info("member = {}", member);
            log.info("=> member.class --> {}", member.getId());
        }
    }

    /**
     * JPQL 을 사용하는 방법
     */
    @Test
    void startJPQL() {
        // username 이 "member1" 찾기
        String qlString = "select m " +
                "from Member m " +
                "where m.username = :username";

        Member findByJpql = em.createQuery(qlString, Member.class)
                .setParameter("username", "member1")    // 파라미터 바인딩
                .getSingleResult();

        Assertions.assertThat(findByJpql.getUsername()).isEqualTo("member1");
    }

    /**
     * QueryDSL 을 사용하는 방법
     */
    @Test
    void startQuerydsl() {
        // 기존에는 QMember.member 이런식으로 사용해야 하지만
        // static import 로 member 하나만 사용하게 할 수 있다. (즉, Q-Type 을 활용하는 것)
        Member findByQuerydsl = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))    // 파라미터 바인딩
                .fetchOne();

        Assertions.assertThat(findByQuerydsl.getUsername()).isEqualTo("member1");
    }

    @Test
    public void search() {
        // 날아가는 query
        // select m from Member m where m.username = :member1 and m.age = :age
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1")
                        .and(member.age.eq(10)))
                .fetchOne();

        Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void searchAndParam() {
        // 위 search 함수에서 where 절에 `.and` 하는 것도 가능하지만
        // `,` 으로도 가능하다.
        // `,` 를 사용하면 `null` 을 무시하므로 동적 쿼리 작성시 매우 유용하다.
        Member findMember = queryFactory
                .selectFrom(member)
                .where(
                        member.username.eq("member1"),
                        member.age.eq(10)
                )
                .fetchOne();

        Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    void resultFetch() {
        // List 조회
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();

        // 단건 조회
//        Member fetchOne = queryFactory
//                .selectFrom(member)
//                .fetchOne();

        // 처음 1건 조회
        Member fetchFirst = queryFactory
                .selectFrom(member)
                .fetchFirst();

        // 페이징 처리시 사용
        // 쿼리가 2번 나간다. (1번째: List 조회 쿼리, 2번째: count 쿼리)
        // 하지만 지원 중단이라고 뜨네?
        QueryResults<Member> fetchResults = queryFactory
                .selectFrom(member)
                .fetchResults();

        // count 쿼리
        // 하지만 지원 중단이라고 뜨네?
        long fetchCount = queryFactory
                .selectFrom(member)
                .fetchCount();
    }

    /**
     * 회원 정렬 순서
     * 1. 회원 나이 내림차순 (desc)
     * 2. 회원 이름 올림차순 (asc)
     * 단, 2에서 회원 이름이 없으면 마지막에 출력 (nulls last)
     */
    @Test
    void sort() {
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(
                        member.age.desc(),                  // 1번 조건
                        member.username.asc().nullsLast()   // 2번 조건
                )
                .fetch();

        Member member5 = result.get(0);
        Member member6 = result.get(1);
        Member memberNull = result.get(2);
        Assertions.assertThat(member5.getUsername()).isEqualTo("member5");
        Assertions.assertThat(member6.getUsername()).isEqualTo("member6");
        Assertions.assertThat(memberNull.getUsername()).isEqualTo(null);
    }

    /**
     * 페이징 처리 (offset, limit)
     */
    @Test
    void paging1() {
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetch();

        Assertions.assertThat(result.size()).isEqualTo(2);
    }

    /**
     * 페이징 처리 2 (fetchResults)
     * fetchResult 를 사용하면
     * count 쿼리와 List 조회 쿼리 두개가 같이 나가는데
     * join 이나 where 같은 제약 조건이 붙게 되면
     * count 쿼리와 조회 쿼리 모두 붙기 때문에 성능상 애매할 수 있어
     * fetchResult 를 사용하는 것보다
     * count 쿼리와 조회 쿼리를 나누는 것이 좋다.
     */
    @Test
    void paging2() {
        QueryResults<Member> fetchResults = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetchResults();

        Assertions.assertThat(fetchResults.getTotal()).isEqualTo(4);
        Assertions.assertThat(fetchResults.getLimit()).isEqualTo(2);
        Assertions.assertThat(fetchResults.getOffset()).isEqualTo(1);
        Assertions.assertThat(fetchResults.getResults().size()).isEqualTo(2);
    }

    /**
     * 집합
     */
    @Test
    void aggregation() {
        // 실제로는 Tuple 보다는 DTO 로 뽑아온다.
        List<Tuple> result = queryFactory
                .select(
                        member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min()
                )
                .from(member)
                .fetch();

        Tuple tuple = result.get(0);

        Assertions.assertThat(tuple.get(member.count())).isEqualTo(4);
        Assertions.assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        Assertions.assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        Assertions.assertThat(tuple.get(member.age.max())).isEqualTo(40);
        Assertions.assertThat(tuple.get(member.age.min())).isEqualTo(10);
    }

    /**
     * 팀의 이름과 각 팀의 평균 연령을 구하기
     */
    @Test
    void group() {
        // given
        List<Tuple> result = queryFactory
                .select(
                        team.name,
                        member.age.avg()
                )
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch();

        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        Assertions.assertThat(teamA.get(team.name)).isEqualTo("teamA");
        Assertions.assertThat(teamA.get(member.age.avg())).isEqualTo(15);   // (10 + 20) / 2

        Assertions.assertThat(teamB.get(team.name)).isEqualTo("teamB");
        Assertions.assertThat(teamB.get(member.age.avg())).isEqualTo(35);   // (30 + 40) / 2
    }

    /**
     * teamA 에 포함된 모든 회원 찾기 (join)
     */
    @Test
    void join() {
        List<Member> result = queryFactory
                .selectFrom(member)
                .join(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();

        Assertions.assertThat(result)
                .extracting("username")
                .containsExactly("member1", "member2");
    }

    /**
     * 세타 조인 (연관 관계가 없는 필드로 JOIN)
     * ex) 회원의 이름이 팀 이름과 같은 회원 조회
     * 하지만, 세타 조인은 외부 join 이 되지 않는다.
     */
    @Test
    void theta_join() {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        List<Member> result = queryFactory
                .select(member)
                .from(member, team)
                .where(member.username.eq(team.name))
                .fetch();

        Assertions.assertThat(result)
                .extracting("username")
                .containsExactly("teamA", "teamB");
    }

    /**
     * ex) 회원과 팀을 조인하면서, 팀 이름이 teamA 인 팀만 조인, 회원은 모두 조회
     * JPQL: select m, t from Member m left join m.team t on t.name = 'teamA'
     */
    @Test
    void join_on_filtering() {
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team)
                .on(team.name.eq("teamA"))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tupple = " + tuple);
        }

    }

    /**
     * 연관 관계가 없는 엔티티 외부 조인
     * ex) 회원의 이름이 팀 이름과 같은 회원 조회
     */
    @Test
    void join_on_no_relation() {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(team)
                .on(member.username.eq(team.name))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    @PersistenceUnit
    EntityManagerFactory emf;

    /**
     * 패치 조인 미적용
     */
    @Test
    void fetchJoinNo() {
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .join(member.team, team)
                .where(member.username.eq("member1"))
                .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        Assertions.assertThat(loaded).as("패치 조인 미적용").isFalse();
    }

    /**
     * 패치 조인 적용
     */
    @Test
    void fetchJoinUse() {
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("member1"))
                .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        Assertions.assertThat(loaded).as("패치 조인 적용").isTrue();
    }

    // subQuery 는 from 절에서 사용할 수 없다. (QueryDSL 은 JPA 빌더의 역할이라 JPA 에서 지원하지 않는 것은 사용 불가)
    // from 절의 서브 쿼리 해결 방안
    // 1. subQuery 를 `join` 으로 변경한다.
    // 2. 애플리케이션에서 쿼리를 2번 분리해서 날린다(실행한다).
    // 3. nativeSQL 을 사용한다.

    /**
     * subQuery
     * ex) 나이가 가장 많은 회원 조회
     */
    @Test
    void subQuery() {

        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(
                        JPAExpressions
                                .select(memberSub.age.max())
                                .from(memberSub)
                ))
                .fetch();

        Assertions.assertThat(result)
                .extracting("age")
                .containsExactly(40);
    }

    /**
     * subQuery - goe
     * ex) 나이가 평균 이상인 회원 조회
     */
    @Test
    void subQueryGoe() {

        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.goe(
                        JPAExpressions
                                .select(memberSub.age.avg())
                                .from(memberSub)
                ))
                .fetch();

        Assertions.assertThat(result)
                .extracting("age")
                .containsExactly(30, 40);
    }

    /**
     * subQuery - in
     * ex) 나이가 10 이상인 쿼리
     */
    @Test
    void subQueryIn() {

        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.in(
                        JPAExpressions
                                .select(memberSub.age)
                                .from(memberSub)
                                .where(memberSub.age.gt(10))
                ))
                .fetch();

        Assertions.assertThat(result)
                .extracting("age")
                .containsExactly(20, 30, 40);
    }

    /**
     * subQuery - select
     * ex) 나이가 10 이상인 쿼리
     */
    @Test
    void selectSubQuery() {

        QMember memberSub = new QMember("memberSub");

        List<Tuple> result = queryFactory
                .select(member.username,
                        JPAExpressions
                                .select(memberSub.age.avg())
                                .from(memberSub))
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    /**
     * QueryDSL case 문
     * (DB 에서 case 를 사용하는 건 좋지 않다.
     * DB 에서는 최소한의 필터링을 하는 것이 좋고, 나머지는 애플리케이션에서 하는 편이 좋다.)
     */
    @Test
    void basicCase() {
        List<String> result = queryFactory
                .select(member.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타"))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    /**
     * 복잡한 case 문
     */
    @Test
    void complexCase() {
        List<String> result = queryFactory
                .select(new CaseBuilder()
                        .when(member.age.between(0, 20)).then("0~20살")
                        .when(member.age.between(21, 30)).then("21~30살")
                        .otherwise("기타"))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    /**
     * 상수 더하기
     */
    @Test
    void constant() {
        List<Tuple> result = queryFactory
                .select(member.username, Expressions.constant("A"))
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    /**
     * 문자 더하기 (concat)
     * 문자가 아닌 다른 Type 인 경우 `.StringValue` 로
     * concat 을 사용할 수 있다.
     */
    @Test
    void concat() {
        // {username}_{age}
        List<String> result = queryFactory
                .select(member.username.concat("_").concat(member.age.stringValue()))
                .from(member)
                .where(member.username.eq("member1"))
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }
}
