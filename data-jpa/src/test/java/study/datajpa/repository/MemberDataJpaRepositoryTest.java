package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
@SpringBootTest
class MemberDataJpaRepositoryTest {

    @Autowired MemberDataJpaRepository memberDataJpaRepository;
    @Autowired TeamDataJpaRepository teamDataJpaRepository;
    @Autowired EntityManager em;

    @Test
    void testMember() {
        // given
        Member member = Member.builder().username("memberA").build();
        Member savedMember = memberDataJpaRepository.save(member);

        // when
        Member findMember = memberDataJpaRepository.findById(member.getId()).get();

        // then
        assertAll(
                () -> assertThat(findMember).isEqualTo(member),
                () -> assertThat(findMember.getId()).isEqualTo(member.getId()),
                () -> assertThat(findMember.getUsername()).isEqualTo(member.getUsername())
        );
    }

    @Test
    void basicCRUD() {
        // given
        Member member1 = Member.builder().username("member1").build();
        Member member2 = Member.builder().username("member2").build();
        memberDataJpaRepository.save(member1);
        memberDataJpaRepository.save(member2);

        // 단건 조회 테스트
        Member findMember1 = memberDataJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberDataJpaRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 테스트
        List<Member> result = memberDataJpaRepository.findAll();
        assertThat(result.size()).isEqualTo(2);

        // 카운트 테스트
        long count = memberDataJpaRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 테스트
        memberDataJpaRepository.delete(member1);
        memberDataJpaRepository.delete(member2);
        long deleteCount = memberDataJpaRepository.count();
        assertThat(deleteCount).isEqualTo(0);
    }

    @Test
    void findByUsernameAndGreaterThenAge() {
        // given
        Member member1 = Member.builder().username("member").age(10).build();
        Member member2 = Member.builder().username("member").age(20).build();
        memberDataJpaRepository.save(member1);
        memberDataJpaRepository.save(member2);

        // when
        List<Member> result = memberDataJpaRepository.findByUsernameAndAgeGreaterThan("member", 15);

        // then
        assertThat(result.get(0).getUsername()).isEqualTo("member");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void findByUsername() {
        // given
        Member member1 = Member.builder().username("member1").age(10).build();
        Member member2 = Member.builder().username("member2").age(20).build();
        memberDataJpaRepository.save(member1);
        memberDataJpaRepository.save(member2);

        // when
        List<Member> result = memberDataJpaRepository.findByUsername("member1");
        Member findMember = result.get(0);

        // then
        assertThat(findMember.getUsername()).isEqualTo("member1");
        assertThat(findMember.getAge()).isEqualTo(10);
    }

    /**
     * Data JPA 의 `@Query` 테스트
     */
    @Test
    void findUser() {
        // given
        Member member1 = Member.builder().username("aaa").age(10).build();
        Member member2 = Member.builder().username("bbb").age(10).build();
        memberDataJpaRepository.save(member1);
        memberDataJpaRepository.save(member2);

        // when
        List<Member> result = memberDataJpaRepository.findUser("aaa", 10);
        Member findMember = result.get(0);

        // then
        assertThat(findMember.getUsername()).isEqualTo("aaa");
        assertThat(findMember.getAge()).isEqualTo(10);
        assertThat(result.size()).isEqualTo(1);
    }

    /**
     * username 필드만 가져오기
     */
    @Test
    void findUsernameList() {
        // given
        Member member1 = Member.builder().username("member1").age(10).build();
        Member member2 = Member.builder().username("member2").age(10).build();
        memberDataJpaRepository.save(member1);
        memberDataJpaRepository.save(member2);

        // when
        List<String> usernameList = memberDataJpaRepository.findUsernameList();

        // then
        assertThat(usernameList.size()).isEqualTo(2);
        assertThat(usernameList).containsExactlyInAnyOrder("member1", "member2");
    }

    /**
     * dto 로 조회하기
     */
    @Test
    void findMemberDto() {
        // given
        Team teamA = Team.builder().teamName("teamA").build();
        teamDataJpaRepository.save(teamA);
        Member member1 = Member.builder().username("member1").age(10).team(teamA).build();
        memberDataJpaRepository.save(member1);

        // when
        List<MemberDto> findMemberDto = memberDataJpaRepository.findMemberDto();

        // then
        assertThat(findMemberDto.size()).isEqualTo(1);
        assertThat(findMemberDto).extracting("username").containsExactly("member1");
    }

    /**
     * 파라미터 바인딩을 통한 `in`절 테스트
     */
    @Test
    void findByNames() {
        // given
        Member member1 = Member.builder().username("AAA").age(10).build();
        Member member2 = Member.builder().username("BBB").age(10).build();
        memberDataJpaRepository.save(member1);
        memberDataJpaRepository.save(member2);

        // when
        List<Member> result = memberDataJpaRepository.findByNames(Arrays.asList("AAA", "BBB"));

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).extracting("username").containsExactly("AAA", "BBB");
    }

    /**
     * Data JPA 반환 타입
     * ! 주의 !
     * JPA 와 달리 Data JPA 에서 단건 조회는 내부에서 try-catch 문을 통해
     * `NoResultException` 을 처리하기 때문에 단건 조회시 데이터가 없을 경우
     * Exception 이 아닌, `null` 을 반환한다.
     * 또, 단건 조회에서 데이터가 2개 이상인 경우 `NonUniqueResultException` 가 터진다.
     * `NonUniqueResultException` 은 spring 내부적으로 `IncorrectResultDataAccessException` 으로 변환하여 반환한다.
     */
    @Test
    void returnType() {
        Member member1 = Member.builder().username("AAA").age(10).build();
        Member member2 = Member.builder().username("BBB").age(10).build();
        memberDataJpaRepository.save(member1);
        memberDataJpaRepository.save(member2);

        List<Member> listResult = memberDataJpaRepository.findListByUsername("AAA");
        Member oneResult = memberDataJpaRepository.findMemberByUsername("AAA");
        Optional<Member> optionalResult = memberDataJpaRepository.findOptionalByUsername("AAA");
    }

    /**
     * Data JPA 의 페이징 처리 (page)
     * API 에서 repository 의 결과 즉, 엔티티를 그대로 반환하게 되면
     * 엔티티가 변경될 때 API 스펙도 같이 변경되므로
     * DTO 를 통해서 반환해야한다.
     * paging 처리된 것은 `map` 함수를 통해 쉽게 DTO 로 변환이 가능하다.
     * (아래의 Tip 참고)
     */
    @Test
    void paging() {
        // given
        memberDataJpaRepository.save(Member.builder().username("member1").age(10).build());
        memberDataJpaRepository.save(Member.builder().username("member2").age(10).build());
        memberDataJpaRepository.save(Member.builder().username("member3").age(10).build());
        memberDataJpaRepository.save(Member.builder().username("member4").age(10).build());
        memberDataJpaRepository.save(Member.builder().username("member5").age(10).build());
        memberDataJpaRepository.save(Member.builder().username("member6").age(10).build());

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "username");

        // when
        Page<Member> pageByAge = memberDataJpaRepository.findPageByAge(age, pageRequest);

        // Tip. map 을 통해 DTO 로 변환하는 방법
        Page<MemberDto> toMap = pageByAge.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        List<Member> content = pageByAge.getContent();
        long totalElements = pageByAge.getTotalElements();

        // then
        assertThat(content.size()).isEqualTo(3);                // 조회된 데이터 수
        assertThat(pageByAge.getTotalElements()).isEqualTo(6);  // 전체 데이터 수
        assertThat(pageByAge.getNumber()).isEqualTo(0);         // 페이지 번호
        assertThat(pageByAge.getTotalPages()).isEqualTo(2);     // 전체 페이지 번호
        assertThat(pageByAge.isFirst()).isTrue();                       // 첫번째 항목인가?
        assertThat(pageByAge.hasNext()).isTrue();                       // 다음 페이지가 있는가?
    }

    /**
     * Data JPA 의 페이징 처리 (slice)
     * `slice` 방식은 Count 쿼리를 사용하지 않고,
     * size + 1 만큼 limit 를 호출하여 다음 페이지가 있는지 없는지 확인한다.
     */
    @Test
    void slice() {
        // given
        memberDataJpaRepository.save(Member.builder().username("member1").age(10).build());
        memberDataJpaRepository.save(Member.builder().username("member2").age(10).build());
        memberDataJpaRepository.save(Member.builder().username("member3").age(10).build());
        memberDataJpaRepository.save(Member.builder().username("member4").age(10).build());
        memberDataJpaRepository.save(Member.builder().username("member5").age(10).build());
        memberDataJpaRepository.save(Member.builder().username("member6").age(10).build());

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "username");

        // when
        Slice<Member> pageByAge = memberDataJpaRepository.findSliceByAge(age, pageRequest);

        List<Member> content = pageByAge.getContent();

        // then
        assertThat(content.size()).isEqualTo(3);           // 조회된 데이터 수
//        assertThat(pageByAge.getTotalElements()).isEqualTo(6);      // 전체 데이터 수
        assertThat(pageByAge.getNumber()).isEqualTo(0);    // 페이지 번호
//        assertThat(pageByAge.getTotalPages()).isEqualTo(2);         // 전체 페이지 번호
        assertThat(pageByAge.isFirst()).isTrue();                   // 첫번째 항목인가?
        assertThat(pageByAge.hasNext()).isTrue();                   // 다음 페이지가 있는가?
    }

    /**
     * Data JPA 벌크 연산 수정 쿼리
     * ! 주의 !
     * bulk 연산은 영속성 컨텍스트의 1차 캐시를 무시하고 DB 에 반영시키므로
     * 동기화 문제가 발생할 수 있다.
     * 따라서, `flush()` 로 데이터베이스에 반영한 뒤, `clear()` 메소드로
     * 영속성 컨텍스트를 초기화하여 1차 캐시를 꼭 비워야 한다.
     * `@Modifying`의 옵션중 `clearAutomatically = true` 로 설정 하면 자동으로 `clear()` 해준다.
     *
     * 참고. JPA 의 벌크 연산의 기본 동작은 JPQL 이 나가기 전에 기본적으로 `flush()` 를 동작한다.
     */
    @Test
    void bulkUpdate() {
        // given
        memberDataJpaRepository.save(Member.builder().username("member1").age(10).build());
        memberDataJpaRepository.save(Member.builder().username("member2").age(19).build());
        memberDataJpaRepository.save(Member.builder().username("member3").age(20).build());
        memberDataJpaRepository.save(Member.builder().username("member4").age(21).build());
        memberDataJpaRepository.save(Member.builder().username("member5").age(40).build());

        // when
        int resultCount = memberDataJpaRepository.bulkAgePlus(20);
//        em.clear();

        // then
        assertThat(resultCount).isEqualTo(3);
    }

    /**
     * member 와 join 되어 있는 team 컬럼은
     * LAZY 즉, 지연 로딩으로 설정되어 있어
     * 첫 쿼리는 Member 만 가져오고, team 은 실제로 가져오지 않고 proxy 즉, 가짜 데이터를 반환.
     * 만약, Member.getTeam() 즉, Member 와 연결된 Team 테이블에 데이터에 접근하려고 할 때,
     * Team 테이블에 대한 쿼리를 보낸다.
     * (위 문제가 바로 N + 1 문제)
     */
    @Test
    void findMemberLazy() {
        // given
        Team teamA = Team.builder().teamName("teamA").build();
        Team teamB = Team.builder().teamName("teamB").build();
        teamDataJpaRepository.save(teamA);
        teamDataJpaRepository.save(teamB);

        memberDataJpaRepository.save(Member.builder().username("member1").age(10).team(teamA).build());
        memberDataJpaRepository.save(Member.builder().username("member2").age(10).team(teamB).build());

        em.flush();
        em.clear();

        // when
//        List<Member> members = memberDataJpaRepository.findMemberFetchJoin();  // N+1 문제 해결 (fetch join)
//        List<Member> members = memberDataJpaRepository.findAll();  // N+1 문제 해결 (Entity Graph)
        List<Member> members = memberDataJpaRepository.findMemberEntityGraph();  // N+1 문제 해결 (`@Query` 와 `@EntityGraph` 를 사용한 방법)
//        memberDataJpaRepository.findEntityGraphByUsername("member1");

        // then
        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("member.getTeam().getTeamName() = " + member.getTeam().getTeamName());
        }
    }

    /**
     * hibernate 의 `@QueryHints`을 통해 readOnly 속성으로 주어
     * 내부적으로 dirty check 즉, 변경 감지를 위해 메모리에 원본(스냅 샷)을 가지고 있는 것을
     * 없애도록 할 수 있다.
     * 따라서, `updateUsername()` 을 통해 변경해도
     * 변경 감지를 위한 원본 스냅샷이 없기 때문에 update 쿼리를 보내지 않는다.
     * (하지만, 성능 최적화는 생각보다 작다.)
     */
    @Test
    void queryHint() {
        // given
        Member member = Member.builder().username("member1").age(10).build();
        memberDataJpaRepository.save(member);
        em.flush();
        em.clear();

        // when
        Member findMember = memberDataJpaRepository.findReadOnlyByUsername("member1").get();
        findMember.updateUsername("member2");

        em.flush();
        // then
    }

    /**
     * JPA Lock
     */
    @Test
    void lock() {
        // given
        Member member = Member.builder().username("member1").age(10).build();
        memberDataJpaRepository.save(member);
        em.flush();
        em.clear();

        // when
        Member findMember = memberDataJpaRepository.findLockByUsername("member1").get();

        // then
    }
}