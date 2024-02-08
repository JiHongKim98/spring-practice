package study.datajpa.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@Transactional
@SpringBootTest
class MemberJpaRepositoryTest {

    @Autowired MemberJpaRepository memberJpaRepository;

    @Test
    void testMember() {
        // given
        Member member = Member.builder().username("memberA").build();
        Member savedMember = memberJpaRepository.save(member);

        // when
        Member findMember = memberJpaRepository.find(savedMember.getId());

        // then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    /**
     * JPA 는 내부에 엔티티의 스냅샷을 가지고 있어 변경감지가 이루어지면
     * 자동으로 update 쿼리를 날린다.
     * 즉, 따로 update 메소드를 생성할 필요가 없다.
     */
    @Test
    void basicCRUD() {
        // given
        Member member1 = Member.builder().username("member1").build();
        Member member2 = Member.builder().username("member2").build();
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        // 단건 조회 테스트
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 테스트
        List<Member> result = memberJpaRepository.findAll();
        assertThat(result.size()).isEqualTo(2);

        // 카운트 테스트
        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 테스트
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);
        long deleteCount = memberJpaRepository.count();
        assertThat(deleteCount).isEqualTo(0);
    }

    @Test
    void findByUsernameAndGreaterThenAge() {
        // given
        Member member1 = Member.builder().username("member").age(10).build();
        Member member2 = Member.builder().username("member").age(20).build();
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        // when
        List<Member> result = memberJpaRepository.findByUsernameAndAgeGreaterThan("member", 15);

        // then
        assertThat(result.get(0).getUsername()).isEqualTo("member");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    /**
     * NamedQuery 테스트
     */
    @Test
    void findByUsername() {
        // given
        Member member = Member.builder().username("member").age(10).build();
        memberJpaRepository.save(member);

        // when
        List<Member> result = memberJpaRepository.findByUsername("member");
        Member findMember = result.get(0);

        // then
        assertThat(findMember.getUsername()).isEqualTo("member");
    }


    /**
     * 순수 JPA 페이징 처리
     */
    @Test
    void paging() {
        // given
        memberJpaRepository.save(Member.builder().username("member1").age(10).build());
        memberJpaRepository.save(Member.builder().username("member2").age(10).build());
        memberJpaRepository.save(Member.builder().username("member3").age(10).build());
        memberJpaRepository.save(Member.builder().username("member4").age(10).build());
        memberJpaRepository.save(Member.builder().username("member5").age(10).build());
        memberJpaRepository.save(Member.builder().username("member6").age(10).build());

        int age = 10;
        int offset = 0;
        int limit = 3;

        // when
        List<Member> memberByPage = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);

        // then
        assertThat(memberByPage.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(6);
    }

    /**
     * JPA 벌크 연산 수정 쿼리
     * ! 주의 !
     * bulk 연산은 영속성 컨텍스트의 1차 캐시를 무시하고 DB 에 반영시키므로
     * 동기화 문제가 발생할 수 있다.
     * 따라서, `flush()` 로 데이터베이스에 반영한 뒤, `clear()` 메소드로
     * 영속성 컨텍스트를 초기화하여 1차 캐시를 꼭 비워야 한다.
     */
    @Test
    void bulkUpdate() {
        // given
        memberJpaRepository.save(Member.builder().username("member1").age(10).build());
        memberJpaRepository.save(Member.builder().username("member2").age(19).build());
        memberJpaRepository.save(Member.builder().username("member3").age(20).build());
        memberJpaRepository.save(Member.builder().username("member4").age(21).build());
        memberJpaRepository.save(Member.builder().username("member5").age(40).build());

        // when
        int resultCount = memberJpaRepository.bulkAgePlus(20);

        // then
        assertThat(resultCount).isEqualTo(3);
    }
}