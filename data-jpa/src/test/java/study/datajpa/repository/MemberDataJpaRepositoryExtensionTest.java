package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.JpaBaseEntity;
import study.datajpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@Transactional
@SpringBootTest
public class MemberDataJpaRepositoryExtensionTest {

    @Autowired EntityManager em;
    @Autowired MemberDataJpaRepository memberDataJpaRepository;

    /**
     * Data JPA 에서 사용자 정의 클래스인 `MemberDataJpaRepositoryCustom` 테스트
     *
     * @see MemberDataJpaRepositoryCustom#findMemberCustom()
     */
    @Test
    void callCustom() {
        // given
        Member member1 = Member.builder().username("member1").age(10).build();
        memberDataJpaRepository.save(member1);
        em.flush();
        em.clear();

        // when
        List<Member> findMember = memberDataJpaRepository.findMemberCustom();
    }

    /**
     * 공통 매핑 정보가 필요한 엔티티들의 상위 클래스로 사용되는 순수 JPA 의 어노테이션
     * `@MappedSuperclass` 을 사용한 JpaBaseEntity 테스트
     *
     * @see JpaBaseEntity#preUpdate()
     */
    @Test
    void JpaEventBaseEntity() throws InterruptedException {
        // given
        Member member = Member.builder().username("member1").age(10).build();
        memberDataJpaRepository.save(member);

        Thread.sleep(200);
        member.updateUsername("member2");

        em.flush();  // `@PreUpdate` 실행
        em.clear();
        
        // when
        Member findMember = memberDataJpaRepository.findById(member.getId()).get();

        // then
        log.info("findMember => {}", findMember);
        log.info("findMember.getCreateDate() => {}", findMember.getCreateDate());
//        log.info("findMember.getUpdateDate() => {}", findMember.getUpdateDate());
        log.info("findMember.getLastModifiedDate() => {}", findMember.getLastModifiedDate());
        log.info("findMember.getCreateBy() => {}", findMember.getCreateBy());
        log.info("findMember.getLastModifiedBy() => {}", findMember.getLastModifiedBy());
    }
}
