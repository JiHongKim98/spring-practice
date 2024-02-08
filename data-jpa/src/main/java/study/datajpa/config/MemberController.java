package study.datajpa.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberDataJpaRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberDataJpaRepository memberDataJpaRepository;

    @GetMapping("/api/v1/members/{id}")
    public String findMemberV1(@PathVariable("id") Long id) {
        Member findMember = memberDataJpaRepository.findById(id).get();
        return findMember.getUsername();
    }

    /**
     * 도메인 클래스 컨버터 사용
     * 도메인 클래스 컨버터는 리파지토리를 사용해서 엔티티를 찾아 반환한다.
     * ! 주의 !
     * 트랜잭션이 없는 범위에서 엔티티를 조회한 것이므로, 엔티티를 값을 변경해도 DB에 반영되지 않는다.
     */
    @GetMapping("/api/v2/members/{id}")
    public String findMemberV2(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    /**
     * 페이징 처리
     * (엔티티를 직접 반환하는 것보다 꼭 DTO 를 통해 반환하자.)
     */
    @GetMapping("/api/v1/members")
    public Page<Member> pagingV1(Pageable pageable) {
        return memberDataJpaRepository.findAll(pageable);
    }

    /**
     * 페이징 처리
     * DTO 를 사용하는 방법
     */
    @GetMapping("/api/v2/members")
    public Page<MemberDto> pagingV2(Pageable pageable) {
        Page<Member> page = memberDataJpaRepository.findAll(pageable);
        return page.map(MemberDto::new);
    }

    /**
     * 페이징 처리
     * PageableDefault 를 통해 기본값을 지정하는 방법 (application.yml 에서 전역 설정도 가능하다)
     */
    @GetMapping("/api/v3/members")
    public Page<MemberDto> pagingV3(@PageableDefault(size = 5) Pageable pageable) {
        Page<Member> page = memberDataJpaRepository.findAll(pageable);
        return page.map(MemberDto::new);
    }

    @PostConstruct
    public void init() {
        memberDataJpaRepository.save(Member.builder().username("member1").age(10).build());

        for (int i=1; i<101; i++) {
            memberDataJpaRepository.save(Member.builder().username("member" + i).age(i).build());
        }
    }
}
