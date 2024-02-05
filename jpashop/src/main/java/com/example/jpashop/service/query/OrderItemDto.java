package com.example.jpashop.service.query;

import com.example.jpashop.domain.OrderItem;
import lombok.Getter;

@Getter
public class OrderItemDto {


    private String itemName;
    private int orderPrice;
    private int count;

    public OrderItemDto(OrderItem orderItem) {
        this.itemName = orderItem.getItem().getName();
        this.orderPrice = orderItem.getOrderPrice();
        this.count = orderItem.getCount();
    }
}
