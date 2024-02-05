package com.example.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Delivery {

    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @JsonIgnore
    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

    private Address address;

    @Enumerated(EnumType.STRING)    // EnumType 의 기본값이 `ORDINAL` 인데 이건 1,2,3 숫자라서 String 으로 꼭 바꿔야 한다.
    private DeliveryStatus status;  // READY, COMPLETE
}
