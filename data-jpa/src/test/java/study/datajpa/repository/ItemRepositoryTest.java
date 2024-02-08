package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Item;

@Transactional
@SpringBootTest
class ItemRepositoryTest {

    @Autowired ItemRepository itemRepository;

    /**
     * 새로운 엔티티를 구별하는 방법 테스트
     * `@GeneratedValue` 을 사용하지 않고, 직접 Id 를 만드는 경우
     * Data JPA 가 새로운 엔티티로 인식하도록 구현하는 방법
     * @see Item#isNew()
     * @see org.springframework.data.jpa.repository.support.SimpleJpaRepository#save(Object)
     */
    @Test
    void save() {
        Item item = new Item("A");
        itemRepository.save(item);
    }

}