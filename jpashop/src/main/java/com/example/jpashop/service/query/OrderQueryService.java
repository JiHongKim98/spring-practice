package com.example.jpashop.service.query;

import com.example.jpashop.api.OrderApiController;
import com.example.jpashop.domain.Order;
import com.example.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderQueryService {

    private final OrderRepository orderRepository;

    public List<OrderDto> orderV3() {
        List<Order> orders = orderRepository.findAllWithItem();

        List<OrderDto> result = orders.stream()
                .map(order -> new OrderDto(order))
                .collect(Collectors.toList());

        return result;
    }
}
