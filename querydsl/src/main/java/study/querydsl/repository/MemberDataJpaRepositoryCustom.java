package study.querydsl.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;

import java.util.List;

public interface MemberDataJpaRepositoryCustom {

    List<MemberTeamDto> search(MemberSearchCondition condition);


    // JPA DATA 페이징 처리 (Pageable - org.springframework.data.domain)

    /**
     * 페이징 처리 1번 방법 (지원 종료)
     * count 쿼리와 result 쿼리를 함께 사용하는 fetchResults 를 사용하는 방법
     * 실제 쿼리는 count 쿼리와 조회 쿼리 두개 가 나간다.
     * groupby having 절 등을 사용하는 복잡한 쿼리문에서 제대로 동작하지 않는다.
     */
    Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable);

    /**
     * 페이징 처리 2번 방법
     * count 쿼리와 result 분리하여 별도로 사용하는 경우
     * size() 를 사용하여 카운트를 만드는 방법도 있지만, OOM(Out-Of-Memory) 오류가 발생할 수 있다.
     * SQL 에서 `count()` 를 지원하므로 OOM 위험이 있는 `size()` 보다는 SQL 의 `count()` 를 사용하는 것이 좋다.
     */
    Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable);
}
