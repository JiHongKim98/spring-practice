package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;

@Slf4j
@SpringBootTest
public class RequiresNewTxTest {

    @Autowired
    PlatformTransactionManager txManager;

    @TestConfiguration
    static class Config {

        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }
    }

    /**
     * REQUIRES_NEW 옵션은 outer 와 inner 트랜잭션을 각각 독립적인 트랜잭션(conn0, conn1)으로 생성한다.
     * 즉, 기존의 outer 트랜잭션은 잠시 `보류`가 되고 inner 트랜잭션을 처리한다.
     *
     * 따라서, outer 는 inner 트랜잭션이 롤백이 되든 말든 관련이 없게 된다.
     */
    @Test
    void RequiresNew() {
        // 외부 트랜잭션
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionDefinition());
        log.info("outer.isNewTransaction() = {}", outer.isNewTransaction());    // true

        // 내부 트랜잭션 (강제로 새로운 트랜잭션 생성)
        log.info("내부 트랜잭션 시작");
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus inner = txManager.getTransaction(definition);
        log.info("outer.isNewTransaction() = {}", inner.isNewTransaction());    // true

        log.info("내부 트랜잭션 롤백");
        txManager.rollback(inner);

        log.info("외부 트랜잭션 커밋");
        txManager.commit(outer);
    }
}
