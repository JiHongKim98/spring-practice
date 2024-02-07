package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Hello;
import study.querydsl.entity.QHello;

@Transactional
@SpringBootTest
class QuerydslApplicationTests {

	@Autowired
	EntityManager em;

	@Test
	void contextLoads() {
		Hello hello = new Hello();
		em.persist(hello);

		// JPAQueryFactory 를 Field 로 빼서 사용하는 것을 권장
		// EntityManager 와 JPAQueryFactory 모두 동시성 문제에 상관없이 설계 되어 있어
		// Field 로 빠져도 된다.
		JPAQueryFactory query = new JPAQueryFactory(em);
		QHello qHello = QHello.hello;

		Hello findHello = query.selectFrom(qHello).fetchOne();

		Assertions.assertThat(findHello).isEqualTo(hello);
		Assertions.assertThat(findHello.getId()).isEqualTo(hello.getId());
	}

}
