package hello.springtx.apply;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * PostConstruct 즉, '초기화 코드가 먼저 실행' 되고 나서
 * AOP(트랜잭션 AOP)가 적용되기 때문에
 * 초기화 시점에서 Method 의 트랜잭션을 얻을 수 없다.
 *
 * 위 문제의 대안은
 * EventListener 를 사용.
 */
@SpringBootTest
public class InitTxTest {

    @Autowired
    Hello hello;

    @Test
    void initStart() {
        // 초기화 코드는 스프링이 초기화 시점에 호출한다.
    }

    @TestConfiguration
    static class InitTxTestConfig {

        @Bean
        Hello hello() {
            return new Hello();
        }
    }

    @Slf4j
    static class Hello {

        @PostConstruct
        @Transactional
        public void initV1() {
            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive(); // output: false
            log.info("Hello init @PostConstruct tx active = {}", isActive);
        }

        @Transactional
        @EventListener(ApplicationReadyEvent.class)
        public void initV2() {
            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive(); // output: true
            log.info("Hello init ApplicationReadyEvent tx active = {}", isActive);
        }
    }
}
