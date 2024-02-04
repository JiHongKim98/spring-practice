package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
class MemberServiceTest {

    @Autowired MemberService memberService;

    @Autowired MemberRepository memberRepository;

    @Autowired LogRepository logRepository;

    /**
     * memberService    @Transactional: OFF
     * memberRepository @Transactional: ON
     * logRepository    @Transactional: ON
     */
    @Test
    void outerTxOff_success() {
        // given
        String username = "outerTxOff_success";

        // when
        memberService.joinV1(username);

        // then
        assertTrue(memberRepository.findByUsername(username).isPresent());
        assertTrue(logRepository.findLog(username).isPresent());
    }

    /**
     * memberService    @Transactional: OFF
     * memberRepository @Transactional: ON
     * logRepository    @Transactional: ON Exception
     */
    @Test
    void outerTxOff_fail() {
        // given
        String username = "로그예외 outerTxOff_fail";

        // when
        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        // then
        // member 는 isPresent 즉, 보유중이어야하고,
        // log 는 isEmpty 즉, 비어있어야한다.
        assertTrue(memberRepository.findByUsername(username).isPresent());
        assertTrue(logRepository.findLog(username).isEmpty());
    }

    /**
     * memberService    @Transactional: ON
     * memberRepository @Transactional: OFF
     * logRepository    @Transactional: OFF
     */
    @Test
    void singleTx() {
        // given
        String username = "singleTx";

        // when
        memberService.joinV1(username);

        // then
        assertTrue(memberRepository.findByUsername(username).isPresent());
        assertTrue(logRepository.findLog(username).isPresent());
    }

    /**
     * memberService    @Transactional: ON
     * memberRepository @Transactional: ON
     * logRepository    @Transactional: ON
     */
    @Test
    void outerTxOn_success() {
        // given
        String username = "outerTxOn_success";

        // when
        memberService.joinV1(username);

        // then
        assertTrue(memberRepository.findByUsername(username).isPresent());
        assertTrue(logRepository.findLog(username).isPresent());
    }

    /**
     * memberService    @Transactional: ON
     * memberRepository @Transactional: ON
     * logRepository    @Transactional: ON Exception
     */
    @Test
    void outerTxOn_fail() {
        // given
        String username = "로그예외 outerTxOn_fail";

        // when
        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        // then
        // log 에서 Exception 이 터져
        // member 와 log 모두 rollback 되어야 한다.
        assertTrue(memberRepository.findByUsername(username).isEmpty());
        assertTrue(logRepository.findLog(username).isEmpty());
    }

    /**
     * memberService    @Transactional: ON
     * memberRepository @Transactional: ON
     * logRepository    @Transactional: ON Exception
     */
    @Test
    void recoverException_fail() {
        // given
        String username = "로그예외 recoverException_fail";

        // when
        assertThatThrownBy(() -> memberService.joinV2(username))
                .isInstanceOf(UnexpectedRollbackException.class);

        // then
        // log 에서 Exception 이 터져
        // member 와 log 모두 rollback 되어야 한다.
        assertTrue(memberRepository.findByUsername(username).isEmpty());
        assertTrue(logRepository.findLog(username).isEmpty());
    }

    /**
     * memberService    @Transactional: ON
     * memberRepository @Transactional: ON
     * logRepository    @Transactional: ON(REQUIRES_NEW) Exception
     */
    @Test
    void recoverException_success() {
        // given
        String username = "로그예외 recoverException_success";

        // when
        memberService.joinV2(username);

        // then
        // log 의 Transactional 이 REQUIRES_NEW 옵션이라 독립된 트랜젝션이므로
        // member 는 저장이 되고 log 는 rollback 이 된다.
        assertTrue(memberRepository.findByUsername(username).isPresent());
        assertTrue(logRepository.findLog(username).isEmpty());
    }

}