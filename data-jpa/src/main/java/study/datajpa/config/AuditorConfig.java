package study.datajpa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@Configuration
@EnableJpaAuditing // Data JPA 의 Auditing 을 사용하기 위한 어노테이션
public class AuditorConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        // 실제로는 spring security 를 통해 Member 를 꺼내와야한다.
        return () -> Optional.of(UUID.randomUUID().toString());
    }
}
