package com.example.jpashop.service;

import com.example.jpashop.domain.Delivery;
import com.example.jpashop.domain.Member;
import com.example.jpashop.domain.Order;
import com.example.jpashop.domain.OrderItem;
import com.example.jpashop.domain.item.Item;
import com.example.jpashop.repository.ItemRepository;
import com.example.jpashop.repository.MemberRepositoryOld;
import com.example.jpashop.repository.OrderRepository;
import com.example.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepositoryOld memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문 조회
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {
        // Entity 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // 주문 상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장
        orderRepository.save(order);

        return order.getId();
    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        // 주문 조회
        Order order = orderRepository.findOne(orderId);

        // 주문 취소
        order.cancel();
    }

    /**
     * 주문 검색
     */
    public List<Order> findOrders(OrderSearch orderSearch) {
//        return orderRepository.findAll(orderSearch);
        return orderRepository.findAllByQueryDsl(orderSearch);  // QueryDSL 버전
    }

}
