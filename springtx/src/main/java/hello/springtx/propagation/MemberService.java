package hello.springtx.propagation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final LogRepository logRepository;

    @Transactional
    public void joinV1(String username) {
        Member member = new Member(username);
        Log logMessage = new Log(username);

        log.info("=== memberRepository 호출 시작 ===");
        memberRepository.save(member);
        log.info("=== memberRepository 호출 종료 ===");

        log.info("=== logRepository 호출 시작 ===");
        logRepository.save(logMessage);
        log.info("=== logRepository 호출 종료 ===");
    }

    /**
     * 로그 저장으로 인해 member 가 저장이 안되는 것을 방지 하기 위해
     * try catch 문으로 잡음
     */
    @Transactional
    public void joinV2(String username) {
        Member member = new Member(username);
        Log logMessage = new Log(username);

        log.info("=== memberRepository 호출 시작 ===");
        memberRepository.save(member);
        log.info("=== memberRepository 호출 종료 ===");


        log.info("=== logRepository 호출 시작 ===");
        try {
            logRepository.save(logMessage);

        } catch (RuntimeException e) {
            log.info("log 저장에 실패했습니다. logMessage = {}", logMessage);
            log.info("try-catch 문으로 정상 흐름으로 변경");

        }
        log.info("=== logRepository 호출 종료 ===");
    }
}
