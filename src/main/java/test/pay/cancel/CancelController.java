package test.pay.cancel;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import test.pay.dto.CancelTestDto;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CancelController {

    private final CancelService cancelService;


    // 토큰 발행
    @GetMapping("/order/getToken")
    public String getToken() throws IOException {

        String token = cancelService.getAccessToken();

        System.out.println(token);

        return "토큰 발급이 완료되었습니다.";
    }


    // 주문 취소
    @PostMapping("/order/cancel")
    public String orderCancel(@RequestBody CancelTestDto cancelTestDto) throws IOException {

        cancelService.refundRequest(cancelTestDto);

        return "주문 취소가 되었습니다.";
    }
}
