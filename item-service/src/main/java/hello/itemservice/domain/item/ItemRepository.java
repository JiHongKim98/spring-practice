package hello.itemservice.domain.item;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ItemRepository {

    /**
     * 실제 구현할 때는 동시에 여러 쓰레드가 접근하므로 `HashMap` 과 `Long` 을 사용하면 안된다.
     */
    private static final Map<Long, Item> store = new HashMap<>();  // ConcurrentHashMap
    private static long sequence = 0L;  // AtomicLong 을 사용해야한다.

    public Item save(Item item) {
        item.setId(++sequence);
        store.put(item.getId(), item);
        return item;
    }

    public Item findById(long id) {
        return store.get(id);
    }

    public List<Item> findAll() {
        return new ArrayList<>(store.values());
    }

    /**
     * 원래는 DTO를 구현해서 Dto로 매핑해야함.
     */
    public void update(long id, Item updateParam) {
        Item nowItem = store.get(id);

        nowItem.setItemName(updateParam.getItemName());
        nowItem.setPrice(updateParam.getPrice());
        nowItem.setQuantity(updateParam.getQuantity());
    }

    public void clearStore() {
        store.clear();
    }
}
