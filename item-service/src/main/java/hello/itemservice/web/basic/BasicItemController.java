package hello.itemservice.web.basic;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor  // final 필드의 생성자를 만들어준다. (Bean 에 등록됨)
public class BasicItemController {

    private final ItemRepository itemRepository;

//    @Autowired  // Spring Bean에 등록
//    public BasicItemController(ItemRepository itemRepository) {
//        this.itemRepository = itemRepository;
//    }

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "basic/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/item";
    }

    @GetMapping("/add")
    public String addForm() {
        return "basic/addForm";
    }

    //@PostMapping("/add")
    public String addItemV1(
            @RequestParam String itemName,
            @RequestParam int price,
            @RequestParam int quantity,
            Model model
    ) {
        Item item = new Item(itemName, price, quantity);
        itemRepository.save(item);

        model.addAttribute("item", item);

        return "basic/addForm";
    }

    //@PostMapping("/add")
    public String addItemV2(@ModelAttribute("item") Item item) {
        itemRepository.save(item);

//        model.addAttribute("item", item);  // @ModelAttribute에서 자동으로 추가 (생략 가능)

        return "basic/addForm";
    }

    //@PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item) {
        itemRepository.save(item);
        return "basic/addForm";
    }

    //@PostMapping("/add")
    public String addItemV4(Item item) {
        itemRepository.save(item);
        return "basic/addForm";
    }

    //@PostMapping("/add")
    public String addItemV5(Item item) {
        itemRepository.save(item);
        return "redirect:/basic/items";
    }

    @PostMapping("/add")
    public String addItemV6(Item item, RedirectAttributes redirectAttributes) {
        Item savedItem = itemRepository.save(item);

        // itemId 는 {itemId}에 있으므로 pathVariable
        // status 는 query param 으로 전달된다.
        // URL => /basic/items/3?status=true
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);

        return "redirect:/basic/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/basic/items/{itemId}";
    }

    /**
     * Test 용 데이터
     */
    @PostConstruct
    public void init() {
        itemRepository.save(new Item("itemA", 20, 20000));
        itemRepository.save(new Item("itemB", 12, 9000));
    }
}
