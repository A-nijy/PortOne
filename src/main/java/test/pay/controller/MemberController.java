package test.pay.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class MemberController {

    // 메인 홈 페이지 반환
    @GetMapping("/")
    public String home() {

        log.info("Get 메서드로 / 주소에 접근하여 컨트롤러 호출됨");
        log.info("MemberController -> home() 호출됨 -> home.html 반환");
        return "home";
    }
}
