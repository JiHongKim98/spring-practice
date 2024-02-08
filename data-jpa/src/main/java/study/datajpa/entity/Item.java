package study.datajpa.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * `@GeneratedValue` 을 사용하지 않고 직접 id 를 하는 경우
 * 새로운 엔티티를 만들었을 때, Data JPA 가 새로운 엔티티라고
 * 인식하기 위해서는 생성일의 유무를 기반으로 새로운 엔티티 인지 아닌지 확인 해야한다.
 * 따라서, `Persistable` 의 `isNew()` 메소드에서 `return createdDate == null;`
 * 즉, 생성일이 있으면 `em.merge()`, 생성일이 없으면 `em.persist()` 를 호출하도록 해야한다.
 * @see org.springframework.data.jpa.repository.support.SimpleJpaRepository#save(Object)
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item implements Persistable<String> {

    @Id //@GeneratedValue
    private String id;

    @CreatedDate
    private LocalDateTime createdDate;

    public Item(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public boolean isNew() {
        return createdDate == null;  // 새로운 엔티티 생성 여부를 확인하는 로직
    }
}
