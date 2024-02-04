package hello.springtx.order;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter     // 실제로는 데이터를 변경하는 setter 를 '남발하는 것은 좋지' 않다.
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue
    private Long id;

    private String username;    // 정상, 예외, 잔고부족
    private String payStatus;   // 대기, 완료
}
