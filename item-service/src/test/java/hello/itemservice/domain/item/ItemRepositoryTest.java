package hello.itemservice.domain.item;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class ItemRepositoryTest {

    ItemRepository itemRepository = new ItemRepository();

    @AfterEach
    void afterEach() {
        itemRepository.clearStore();
    }

    @Test
    void save() {
        // given
        Item item = new Item("itemA", 10000, 20);

        // when
        Item saveItem = itemRepository.save(item);

        // then
        Item findItem = itemRepository.findById(item.getId());
        Assertions.assertThat(findItem).isEqualTo(saveItem);
    }

//    @Test
//    void findById() {
//        // given
//
//        // when
//
//        // then
//    }

    @Test
    void findAll() {
        // given
        Item item1 = new Item("item1", 10000, 10);
        Item item2 = new Item("item2", 20000, 20);

        itemRepository.save(item1);
        itemRepository.save(item2);

        // when
        List<Item> result = itemRepository.findAll();

        // then
        Assertions.assertThat(result.size()).isEqualTo(2);
        Assertions.assertThat(result).contains(item1, item2);
    }

    @Test
    void update() {
        // given
        Item item = new Item("itemA", 10000, 20);

        Item saveItem = itemRepository.save(item);
        Long itemId = saveItem.getId();

        // when
        Item updateParam = new Item("itemB", 20000, 40);
        itemRepository.update(itemId, updateParam);

        // then
        Item findItem = itemRepository.findById(item.getId());
        Assertions.assertThat(findItem.getItemName()).isEqualTo(updateParam.getItemName());
        Assertions.assertThat(findItem.getPrice()).isEqualTo(updateParam.getPrice());
        Assertions.assertThat(findItem.getPrice()).isEqualTo(updateParam.getPrice());
    }
}