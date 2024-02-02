package hello.itemservice.repository.mybatis;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemSearchCondition;
import hello.itemservice.repository.ItemUpdateDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ItemMapper {

    // 파라미터가 1개 인경우는 @Param 을 생략할 수 있지만 2개 이상인 경우 생략 불가능

    void save(Item item);

    void update(@Param("id") Long id, @Param("updateParam") ItemUpdateDto itemUpdateDto);

    List<Item> findAll(ItemSearchCondition cond);

    Optional<Item> findById(Long id);
}
