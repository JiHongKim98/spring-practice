package com.example.jpashop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("Album")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter @Setter
public class Album extends Item {

    private String artist;
    private String etc;
}
