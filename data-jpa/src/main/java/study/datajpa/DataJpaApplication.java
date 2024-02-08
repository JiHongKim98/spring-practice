package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import study.datajpa.config.AuditorConfig;

//@EnableJpaAuditing  // Data JPA 의 Auditing 을 사용하기 위한 어노테이션
@SpringBootApplication
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

	/**
	 * config 로 옮김
	 * @see study.datajpa.config.AuditorConfig
	 */
//	@Bean
//	public AuditorAware<String> auditorProvider() {
//		return () -> Optional.of(UUID.randomUUID().toString());
//	}
}
