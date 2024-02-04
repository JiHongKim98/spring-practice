package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;

/**
 * Required(default 값) 옵션
 * 기존 트랜잭션이 없다 - 새 트랜잭션 생성
 * 기존 트랜잭션이 있다 - 기존 트랜잭션에 참여
 *
 * 트랜잭션 전파 옵션 (isolation , timeout , readOnly) 은 `새 트랜잭션이 생성되는 시점에만` 적용된다.
 */
@Slf4j
@SpringBootTest
public class Required {

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
     * 신규 트랜잭션(isNewTransaction = true)인 경우에만 물리적 커밋을 진행한다.
     */
    @Test
    void innerCommit() {
        // 외부 트랜잭션
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionDefinition());
        log.info("outer.isNewTransaction() = {}", outer.isNewTransaction());    // true

        // 내부 트랜잭션
        log.info("내부 트랜잭션 시작");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionDefinition());
        log.info("outer.isNewTransaction() = {}", inner.isNewTransaction());    // false

        // 내부 트랜잭션을 Commit 하면 트랜잭션이 종료 되기 때문에,
        // 실제로 Commit 은 진행하지 않고 외부 트랜잭션이 종료될 때 Commit 한다.
        log.info("내부 트랜잭션 커밋 시작");
        txManager.commit(inner);    // 논리적 커밋

        log.info("외부 트랜잭션 커밋 시작");
        txManager.commit(outer);    // 물리적 커밋
    }

    /**
     * 내부 또는 외부 트랜잭션중 '하나라도 Rollback 되면 모두 물리적으로 Rollback' 된다.
     */
    @Test
    void outerRollback() {
        // 외부 트랜잭션
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionDefinition());
        log.info("outer.isNewTransaction() = {}", outer.isNewTransaction());    // true

        // 내부 트랜잭션
        log.info("내부 트랜잭션 시작");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionDefinition());
        log.info("outer.isNewTransaction() = {}", inner.isNewTransaction());    // false

        log.info("내부 트랜잭션 커밋 시작");
        txManager.commit(inner);

        log.info("외부 트랜잭션 롤백 시작");
        txManager.rollback(outer);
    }

    /**
     * 내부 트랜잭션에서 Rollback 이 일어나면 'rollback-Only 트랜잭션' 으로 변경된다.
     */
    @Test
    void innerRollback() {
        // 외부 트랜잭션
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionDefinition());
        log.info("outer.isNewTransaction() = {}", outer.isNewTransaction());    // true

        // 내부 트랜잭션
        log.info("내부 트랜잭션 시작");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionDefinition());
        log.info("outer.isNewTransaction() = {}", inner.isNewTransaction());    // false

        log.info("내부 트랜잭션 롤백 시작");
        txManager.rollback(inner);

        // UnexpectedRollbackException 즉, rollback-only 인 오류를 명확하게 반환.
        log.info("외부 트랜잭션 커밋 시작");
        Assertions.assertThatThrownBy(() -> txManager.commit(outer))
                .isInstanceOf(UnexpectedRollbackException.class);
    }
}
