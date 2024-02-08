package study.datajpa.repository;

import study.datajpa.entity.Member;

import java.util.List;

public interface MemberDataJpaRepositoryCustom {

    List<Member> findMemberCustom();
}
