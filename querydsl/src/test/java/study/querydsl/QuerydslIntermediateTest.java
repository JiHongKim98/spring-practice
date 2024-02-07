package study.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberDto;
import study.querydsl.dto.QMemberDto;
import study.querydsl.dto.UserDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import java.util.List;

import static study.querydsl.entity.QMember.member;

@Slf4j
@Transactional
@SpringBootTest
public class QuerydslIntermediateTest {

    @Autowired
    EntityManager em;

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
     * 프로젝션 대상이 하나인 경우
     */
    @Test
    void simpleProjection() {
        List<String> result = queryFactory
                .select(member.username)
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    /**
     * 프로젝션 대상이 둘 이상인 경우 -> tuple
     * 하지만, repository 안에서만 사용하도록 하는 편이 좋다.
     * 이유 -> service 나 다른 계층으로 tuple 을 반환 한다면,
     *        QueryDSL 이 아닌 다른 것으로 변경이 힘들다.
     *        따라서 DTO 를 사용하는 것이 좋다.
     *        (요약, tuple 은 QueryDSL 에 종속되어 있기 때문)
     */
    @Test
    void tupleProjection() {
        List<Tuple> result = queryFactory
                .select(member.username, member.age)
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            String username = tuple.get(member.username);
            Integer age = tuple.get(member.age);
            System.out.println("username = " + username);
            System.out.println("age = " + age);
        }
    }

    /**
     * Tuple 이 아닌 DTO 를 통한 반환 (JPQL 버전)
     */
    @Test
    void findDtoByJPQL() {
        List<MemberDto> result = em.createQuery(
                        "select new study.querydsl.dto.MemberDto(m.username, m.age) from Member m", MemberDto.class)
                .getResultList();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    /**
     * Tuple 이 아닌 DTO 를 통한 반환 (QueryDSL - setter 버전)
     */
    @Test
    void findDtoBySetter() {
        List<MemberDto> result = queryFactory
                .select(Projections.bean(MemberDto.class,   // Querydsl 빈 생성 (QBean) - DTO 에 기본 생성자를 생성해야한다.
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    /**
     * Tuple 이 아닌 DTO 를 통한 반환 (QueryDSL - field 버전)
     */
    @Test
    void findDtoByField() {
        List<MemberDto> result = queryFactory
                .select(Projections.fields(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    /**
     * Tuple 이 아닌 DTO 를 통한 반환 (QueryDSL - 생성자 버전)
     */
    @Test
    void findDtoByConstructor() {
        List<MemberDto> result = queryFactory
                .select(Projections.constructor(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    /**
     * Tuple 이 아닌 DTO 를 통한 반환 (QueryDSL - field 버전)
     * DTO 의 필드와 DB 의 컬럼명이 다를 때
     */
    @Test
    void findUserDtoByField() {
        QMember memberSub = new QMember("memberSub");

        List<UserDto> result = queryFactory
                .select(Projections.fields(UserDto.class,
                        member.username.as("name"), // DB 에는 username 이지만, DTO 는 name 이므로 `as` 를 통해 매핑

                        // age 대신 sub 쿼리로
                        ExpressionUtils.as(JPAExpressions
                                .select(memberSub.age.max())
                                    .from(memberSub), "age")
                        )
                )
                .from(member)
                .fetch();

        for (UserDto userDto : result) {
            System.out.println("userDto = " + userDto);
        }
    }

    /**
     * Tuple 이 아닌 DTO 를 통한 반환 (QueryDSL - 생성자 버전의 확장)
     * QueryProjection 을 통해 DTO 자체를 Q 로 생성하여 사용하는 방법
     * 장점:
     *      Constructor 는 실제로 실행 해봐야 오류를 잡을 수 있지만 (runtime Exception)
     *      QueryProjection 은 실행하기 전 컴파일 단계에서 오류를 잡을 수 있는 장점이 있다 (compile Exception)
     * 단점 :
     *      `@QueryProjection` 어노테이션으로 인해
     *      DTO 가 QueryDSL 의 존재를 알게된다. (DTO 에 QueryDSL 의 의존성이 생겨버림)
     * 즉, Trade OFF 를 생각해야함.
     */
    @Test
    void findDtoQueryProjection() {
        List<MemberDto> result = queryFactory
                .select(new QMemberDto(member.username, member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    /**
     * `BooleanBuilder` 를 사용한 동적 쿼리
     */
    @Test
    void dynamicQuery_BooleanBuilder() {
        String usernameParam = "member1";
        Integer ageParam = 10;

        List<Member> result = searchMember1(usernameParam, ageParam);

        Assertions.assertThat(result.size()).isEqualTo(1);

    }

    // dynamicQuery_BooleanBuilder 의 동적 쿼리 (검색 조건)
    private List<Member> searchMember1(String usernameParam, Integer ageParam) {

        // BooleanBuilder 에 초기값, and, or 등을 넣을 수 있다.
        // ex) new BooleanBuilder(member.username.eq(usernameParam));  -> 기본적으로 사용자 이름과 일치하는 것을 찾음.
        BooleanBuilder builder = new BooleanBuilder();

        // 동적 쿼리 1. username 조건이 있으면 builder 에 조건 추가
        if (usernameParam != null) {
            builder.and(member.username.eq(usernameParam));
        }

        // 동적 쿼리 2. age 조건이 있으면 builder 에 조건 추가
        if (ageParam != null) {
            builder.and(member.age.eq(ageParam));
        }

        return queryFactory
                .selectFrom(member)
                .where(builder)
                .fetch();
    }

    /**
     * `where` 를 사용한 동적 쿼리 처리 방법
     * 장점:
     *      usernameEq, ageEq 등 만든 메소드를 다른 쿼리에서도 재사용이 가능하다.
     *      메소드를 뽑음으로 써, 가독성이 좋아짐.
     *      조합(allEq와 같이) 이 가능하다.
     * 단점:
     */
    @Test
    void dynamicQuery_WhereParam() {
        String usernameParam = "member1";
        Integer ageParam = 10;

        List<Member> result = searchMember2(usernameParam, ageParam);

        Assertions.assertThat(result.size()).isEqualTo(1);

    }

    // dynamicQuery_WhereParam 의 동적 쿼리 (검색 조건)
    private List<Member> searchMember2(String usernameParam, Integer ageParam) {
        return queryFactory
                .selectFrom(member)
//                .where(usernameEq(usernameParam), ageEq(ageParam))
                .where(allEq(usernameParam, ageParam))
                .fetch();
    }

    // searchMember2 의 조건.1 - usernameParam
    private BooleanExpression usernameEq(String usernameParam) {
//        if (usernameParam == null) {
//            return null;
//        }
//        return member.username.eq(usernameParam);
        return usernameParam != null ? member.username.eq(usernameParam) : null;    // 삼항 연산자 버전
    }

    // searchMember2 의 조건.2 - ageParam
    private BooleanExpression ageEq(Integer ageParam) {
        return ageParam != null ? member.age.eq(ageParam) : null;   // 삼항 연산자 버전
    }

    // 위 2개의 조건을 조합하여 dynamicQuery_WhereParam 에서 한번에 사용 가능
    // 하지만, 조합하여 사용할 때 (allEq) 사용시 `null 처리는 주의`해야함.
    private BooleanExpression allEq(String usernameParam, Integer ageParam) {
        return usernameEq(usernameParam).and(ageEq(ageParam));
    }

    /**
     * bulk 연산 (수정, 삭제 배치 쿼리) - 쿼리 한번으로 대량 데이터를 수정
     * ! 주의 !
     * bulk 연산시 `Repeatable Read 문제`가 발생할 수 있다. (flush 와 clear 사용으로 해결)
     * 영속성 컨텍스트가 항상 우선이므로 트랜젝션 벌크 연산을 진행했다면
     * flush 를 통해 DB 와 데이터를 맞추는 작업이 필요하다.
     * 맞추지 않는다면, 같은 트랜젝션 내에서 다시 읽어올 때,
     * 변경되지 않는 값으로 바뀌게 된다.
     */
    @Test
    void bulkUpdate() {

        // member1 = 10 -> member1
        // member2 = 20 -> member2
        // member3 = 30 -> member3
        // member4 = 40 -> member4

        // update 쿼리의 반환값은 영향을 받은 row 의 수를 반환한다.
        long count = queryFactory
                .update(member)
                .set(member.username, "비회원")
                .where(member.age.lt(28))
                .execute();

        em.flush(); // ! 중요함 ! - 영속성 컨텍스트와 맞춤
        em.clear(); // ! 중요함 ! - 영속성 컨텍스트와 맞춤

        // member1 = 10 -> 비회원      // 영속성 컨텍스트와 맞추지 않으면 member2 로 변경되지 않음.
        // member2 = 20 -> 비회원      // 영속성 컨텍스트와 맞추지 않으면 member2 로 변경되지 않음.
        // member3 = 30 -> member3 (변경 x)
        // member4 = 40 -> member4 (변경 x)

        List<Member> result = queryFactory
                .selectFrom(member)
                .fetch();

        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
        }
    }

    /**
     * bulk 연산 - 모든 데이터 더하기
     */
    @Test
    void bulkAdd() {
        long count = queryFactory
                .update(member)
                .set(member.age, member.age.add(-1))     // 모든 age +(-1)
                .execute();
    }

    /**
     * bulk 연산 - 조건에 맞는 데이터 지우기
     */
    @Test
    void bulkDelete() {
        long count = queryFactory
                .delete(member)
                .where(member.age.gt(18))
                .execute();
    }

    /**
     * SQL 함수 사용 (stringTemplate)
     * username 필드에서 `member` 를 `M` 으로 변경하기
     */
    @Test
    void sqlFunction1() {
        // SQL: select replace(m.username,'member','M') from member m;
        List<String> result = queryFactory
                .select(Expressions.stringTemplate(
                        "function('replace', {0}, {1}, {2})",
                        member.username, "member", "M"))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    /**
     * SQL 함수 사용 (stringTemplate)
     * username 필드를 소문자로 변경
     * (lower, upper 같은 ansi 표준 함수들은 querydsl 이 상당 부분 이미 내장중)
     */
    @Test
    void sqlFunction2() {
        List<String> result = queryFactory
                .select(Expressions.stringTemplate(
                        "function('lower', {0})",
                        member.username))
                // lower, upper 같은 ansi 표준 함수들은 querydsl 이 상당 부분 내장중
//                .where(member.username.eq(member.username.lower()))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }
}
