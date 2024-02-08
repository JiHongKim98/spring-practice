package study.datajpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

import java.time.LocalDateTime;

// 순수 JPA 를 사용한 Auditing
@Getter
@MappedSuperclass // `@MappedSuperclass` 는 공통 매핑 정보가 필요한 엔티티들의 상위 클래스로 사용되는 JPA 의 어노테이션.
public class JpaBaseEntity {

    @Column(updatable = false)
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    // `@Pre_` 는 _ 하기 전에 발생하는 Event
    // ex) @PrePersist 는 persist 하기전 발생하는 Event

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createDate = now;
        updateDate = now;
    }

    @PreUpdate
    public void preUpdate() {
        updateDate = LocalDateTime.now();
    }
}
