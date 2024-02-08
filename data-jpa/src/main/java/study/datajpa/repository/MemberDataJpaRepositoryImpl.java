package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import java.util.List;

// 구현체 네이밍 규칙은 Data JPA 의 클래스명 + 'Impl' 로 해야한다.
@RequiredArgsConstructor
public class MemberDataJpaRepositoryImpl implements MemberDataJpaRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }
}
