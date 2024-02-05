package com.example.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "")
@Getter @Setter
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded // 내장 Type 사용
    private Address address;

    @JsonIgnore
    @OneToMany(mappedBy = "member") // 연관 관계의 주인 즉, READ ONLY 로 바뀜
    private List<Order> orders = new ArrayList<>();
}
