package com.example.jpashop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("Movie")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter @Setter
public class Movie extends Item {

    private String director;
    private String actor;
}
