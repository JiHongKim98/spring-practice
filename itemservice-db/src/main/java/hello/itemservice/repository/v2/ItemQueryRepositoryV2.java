package hello.itemservice.repository.v2;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.itemservice.domain.Item;
import hello.itemservice.domain.QItem;
import hello.itemservice.repository.ItemSearchCondition;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static hello.itemservice.domain.QItem.*;

@Repository
public class ItemQueryRepositoryV2 {

    private final EntityManager em;

    private final JPAQueryFactory query;

    public ItemQueryRepositoryV2(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    public List<Item> findAll(ItemSearchCondition condition) {
        return query.select(item)
                .from(item)
                .where(
                        likeItemName(condition.getItemName()),
                        maxPrice(condition.getMaxPrice())
                )
                .fetch();
    }

    private BooleanExpression likeItemName(String itemName) {
        if (StringUtils.hasText(itemName)) {
            return QItem.item.itemName.like("%" + itemName + "%");
        }
        return null;
    }

    private BooleanExpression maxPrice(Integer maxPrice) {
        if (maxPrice != null) {
            return QItem.item.price.loe(maxPrice);
        }
        return null;
    }
}
