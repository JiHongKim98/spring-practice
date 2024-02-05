package com.example.jpashop.service;

import com.example.jpashop.domain.item.Book;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ItemUpdateTest {

    @Autowired
    EntityManager em;

    @Test
    void updateTest() {
        Book book = em.find(Book.class, 1L);

        book.setName("fdfdfdfd");


    }
}
