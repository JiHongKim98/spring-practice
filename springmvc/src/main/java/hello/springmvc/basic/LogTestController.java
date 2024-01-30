package hello.springmvc.basic;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j  // lombok이 private final Logger log = LoggerFactory.getLogger(getClass()); 을 자동생성
@RestController
public class LogTestController {

//    private final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping("/log-test")
    public String logTest() {
        String name = "spring";

        // log.trace("string" + "string") 으로 설정하면
        // trace를 사용하지 않을 때도 java의 특성상 미리 '+' 연산이
        // 진행되어 필요없는 리소스를 낭비하게 된다
        //
        // 따라서, ("{}", name) 처럼 파라미터를 넘겨줘야함

        log.trace(" trace log={}", name);
        log.debug(" debug log={}", name);
        log.info(" info log={}", name);
        log.warn(" warn log={}", name);
        log.error(" error log={}", name);

        return "ok";
    }
}
