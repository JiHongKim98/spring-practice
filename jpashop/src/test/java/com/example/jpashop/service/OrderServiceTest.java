package com.example.jpashop.service;

import com.example.jpashop.domain.Address;
import com.example.jpashop.domain.Member;
import com.example.jpashop.domain.Order;
import com.example.jpashop.domain.OrderStatus;
import com.example.jpashop.domain.item.Book;
import com.example.jpashop.exception.NotEnoughStockException;
import com.example.jpashop.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired EntityManager em;

    @Autowired OrderService orderService;

    @Autowired OrderRepository orderRepository;

    /**
     * 상품 주문 Test
     */
    @Test
    void itemOrder() {
        // given
        Member member = createMember();
        Book book = createBook();

        int orderCount = 2;

        // when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // then
        Order getOrder = orderRepository.findOne(orderId);

        assertThat(getOrder.getOrderItems().size()).isEqualTo(1);
        assertThat(getOrder.getTotalPrice()).isEqualTo(10000 * orderCount);
        assertThat(book.getStockQuantity()).isEqualTo(8);
    }

    /**
     * 상품 주문 재고 수량 초과 Test
     */
    @Test
    void orderItemQuantityExceed() {
        // given
        Member member = createMember();
        Book book = createBook();

        int orderCount = 12;

        // when
        Assertions.assertThatThrownBy(() -> orderService.order(member.getId(), book.getId(), orderCount))
                .isInstanceOf(NotEnoughStockException.class)
                .hasMessage("need more stock");

        // then
    }

    /**
     * 상품 주문 취소 Test
     */
    @Test
    void orderCancel() {
        // given
        Member member = createMember();
        Book book = createBook();

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // when
        orderService.cancelOrder(orderId);
        
        // then
        Order findOrder = orderRepository.findOne(orderId);
        assertThat(book.getStockQuantity()).isEqualTo(10);
    }

    private Book createBook() {
        Book book = new Book();
        book.setName("서울 JPA");
        book.setPrice(10000);
        book.setStockQuantity(10);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "경기", "123-123"));
        em.persist(member);
        return member;
    }
}