package com.example.jpashop.api;

import com.example.jpashop.domain.Address;
import com.example.jpashop.domain.Order;
import com.example.jpashop.domain.OrderItem;
import com.example.jpashop.domain.OrderStatus;
import com.example.jpashop.repository.OrderRepository;
import com.example.jpashop.repository.OrderSearch;
import com.example.jpashop.repository.order.query.OrderFlatDto;
import com.example.jpashop.repository.order.query.OrderItemQueryDto;
import com.example.jpashop.repository.order.query.OrderQueryDto;
import com.example.jpashop.repository.order.query.OrderQueryRepository;
import com.example.jpashop.service.query.OrderQueryService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

//    private final OrderQueryService orderQueryService;
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAll(new OrderSearch());

        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(orderItem -> orderItem.getItem().getName());
        }

        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAll(new OrderSearch());

        return orders.stream()
                .map(order -> new OrderDto(order))
                .collect(Collectors.toList());
    }

    /**
     * fetch join 으로 쿼리는 1개만 발생하지만
     * 컬렉션 즉, _ToMany 관계(x: N)인 경우 모든 데이터를 메모리로 `전부 읽어와`
     * 데이터 전송량이 매우 많아 질 수 있고 페이징 처리를 메모리에서 처리하므로
     * out of memory 오류가 발생할 수 있다.
     * ! WARN 경고 발생 !
     */
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();

        List<OrderDto> result = orders.stream()
                .map(order -> new OrderDto(order))
                .collect(Collectors.toList());

        return result;
//        return orderQueryService.orderV3();
    }

    /**
     * 컬렉션(_ToMany) 페이징 처리 방법
     *
     * 페이징 처리에 영향을 주지 않는 _ToOne 관계를(1개 이므로 row 수에 영향을 주지 않는다.)
     * 모두 fetch join 으로 구성하고, 지연 로딩 성능 최적화를 위해
     * _ToMany 관계는 default_batch_fetch_size 옵션 즉, 최대로 가져올 개수를 제한하여
     * 페이징 처리를 실행한다.
     * 쿼리가 1 + 1 + ... 으로 약간 늘어나지만 데이터 전송량이 작다.
     *
     * 즉, 데이터 전송량 vs 네트워크 연결량간의 trade off 를 생각해야한다.
     */
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit
    ) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

        List<OrderDto> result = orders.stream()
                .map(order -> new OrderDto(order))
                .collect(Collectors.toList());

        return result;
    }

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5() {
        return orderQueryRepository.findAllByDto_optimization();
    }

    /**
     * 장점 : 쿼리가 1번
     * 단점 :
     * Order 기준으로 페이징이 불가능
     * 쿼리는 한번이지만 조인으로 인해 DB 에서 애플리케이션에 전달하는 데이터에 중복 데이터가 추가되므로 상황에 따라 V5 보다 더 느릴 수 도 있다.
     */
    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6() {
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();
        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), Collectors.toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue()))
                .collect(Collectors.toList());
    }

    @Getter
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());
        }
    }

    @Getter
    static class OrderItemDto {

        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            this.itemName = orderItem.getItem().getName();
            this.orderPrice = orderItem.getOrderPrice();
            this.count = orderItem.getCount();
        }
    }
}
