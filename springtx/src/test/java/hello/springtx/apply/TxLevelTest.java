package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Transactional 레벨은 '더 자세한 것이 높은 우선 순위를 가진다'
 * ex)  구현체(func) > 구현체(class) > 인터페이스(func) > 인터페이스(class)
 *      하지만, 인터페이스에 Transactional 을 적용하는 것은 Spring 공식 문서에서
 *      권장하지 않는다.
 */
@SpringBootTest
public class TxLevelTest {

    @Autowired LevelService levelService;

    @Test
    void orderTest() {
        levelService.write();
        levelService.read();
    }

    @TestConfiguration
    static class TxLevelTestConfig {
        @Bean
        static LevelService levelService() {
            return new LevelService();
        }
    }

    @Slf4j
    @Transactional(readOnly = true)  // 읽기만 가능한 트랜젝션
    static class LevelService {

        @Transactional(readOnly = false)  // 기본값이며, 읽기 쓰기 둘다 가능
        public void write() {
            log.info("call write");
            printTxInfo();
        }

        public void read() {
            log.info("call read");
            printTxInfo();
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active = {}", txActive);

            boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
            log.info("tx readOnly = {}", readOnly);
        }
    }
}
