package com.example.jpashop.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable // 내장 Type class
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    private String city;

    private String street;

    private String zipcode;

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
