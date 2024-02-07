package study.querydsl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import study.querydsl.entity.Member;

import java.util.List;

public interface MemberDataJpaRepository extends JpaRepository<Member, Long>, MemberDataJpaRepositoryCustom, QuerydslPredicateExecutor<Member> {

    // select m from Member m where m.username = :username
    List<Member> findByUsername(String username);
}
