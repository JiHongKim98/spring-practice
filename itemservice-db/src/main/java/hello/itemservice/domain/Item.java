package hello.itemservice.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity  // JPA Mapping 객체
@Table(name = "item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_name", length = 10)
    private String itemName;

    private Integer price;

    private Integer quantity;


    // JPA 는 기본 생성자가 '필수'다.
    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
