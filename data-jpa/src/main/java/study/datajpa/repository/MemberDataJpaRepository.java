package study.datajpa.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberDataJpaRepository extends JpaRepository<Member, Long>, MemberDataJpaRepositoryCustom {

    // 쿼리 메소드 기능
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    /**
     * Member Entity 에 정의한 NamedQuery 기능
     * `@Query(name = "Member.findByUsername")` 를 생략해도
     * 반환 Type Entity 에 findByUsername 을 찾는다.
     * 즉, 반환 타입 Member, 현재 메소드 명 findByUsername 이므로
     * 자동으로 Member.findByUsername 을 찾는다.
     * 만약 찾지 못할 시, 자동으로 `메소드 이름 기반 쿼리 생성`을 실행한다.
     *
     * 하지만, 실무에서 잘 사용하지 않는 편이다.
     */
//    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    /**
     * 위에서 설명한 `NamedQuery` 기능과 아래의 `@Query` 기능은
     * 컴파일 시점에서 쿼리 파싱을 진행해, `컴파일 시점에서 오류를 발견할 수 있다`는 장점이 있다.
     */
    @Query("select m from Member m where m.username = :username and age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    // username 필드만 가져오기
    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.teamName) " +
            "from Member m join m.team t")
    List<MemberDto> findMemberDto();

    // 파라미터 바인딩을 통한 `in`절
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    // 유연한 반환 타입
    List<Member> findListByUsername(String username); // 컬렉션
    Member findMemberByUsername(String username); // 단건
    Optional<Member> findOptionalByUsername(String username); // 단건 (Optional)

    // 페이징 처리 (Page)
    Page<Member> findPageByAge(int age, Pageable pageable);

    // `@Query` 에서 조회 쿼리와 countQuery 를 나눠서 페이징 처리도 가능하다.
    @Query(value = "select m from Member m",
            countQuery = "select count(m.username) from Member m")
    Page<Member> findMemberAllCountBy(Pageable pageable);

    // 페이징 처리 (Slice)
    Slice<Member> findSliceByAge(int age, Pageable pageable);

    // Data JPA 벌크 연산 수정 쿼리
    // Data JPA 에서는 `@Modifying` 어노테이션을 꼭 넣어서 변경 쿼리인 것을 알려줘야한다.
    @Modifying(clearAutomatically = true)  // `clearAutomatically` 옵션을 통해 자동으로 `em.clear()` 를 날려준다.
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    // `@Query` 어노테이션을 통한 JPA 의 fetch join
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    // `@Query` 와 fetch join 를 사용하는 대신 Data JPA 의 `@EntityGraph` 를 사용한 방법
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    // `@Query` 와 `@EntityGraph` 를 사용한 방법
    @Query("select m from Member m")
    @EntityGraph(attributePaths = {"team"})
    List<Member> findMemberEntityGraph();

    // 메소드 명과 `@EntityGraph`를 사용하는 방법
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    // hibernate 의 `@QueryHints`을 통해 readOnly 속성으로 주어
    // 내부적으로 dirty check 즉, 변경 감지를 위해 메모리에 원본을 가지고 있는 것을
    // 없애도록 할 수 있다.
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Optional<Member> findReadOnlyByUsername(String username);

    // JPA 에서 제공하는 Lock
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Member> findLockByUsername(String username);
}
