package test.pay.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import test.pay.domain.Member;
import test.pay.domain.Order;
import test.pay.domain.repository.MemberRepository;
import test.pay.service.MemberService;
import test.pay.service.OrderService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Controller
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final OrderService orderService;


    // 메세지와 주문번호(merchantUid)를 받아와서 (없어도 됨) 해당 데이터를 가지고 다시 주문 페이지로 이동
    @GetMapping("/order")
    public String order(@RequestParam(name = "message", required = false) String message,
                        @RequestParam(name = "merchantUid", required = false) String merchantUid,
                        Model model) {

        log.info("Get 메서드로 /order 주소에 접근하여 컨트롤러 호출됨");
        log.info("OrderController -> order() 호출됨 -> 모델에 message와 merchantUid 담을 예정");
        model.addAttribute("message", message);
        model.addAttribute("merchantUid", merchantUid);

        log.info("모델에 message와 merchantUid 담음 -> order.html 반환");
        return "order";
    }


    // 주문 페이지에서 주문하기 버튼을 누르면 동작
    @PostMapping("/order")
    public String autoOrder() {

        log.info("Post 메서드로 /order 주소에 접근하여 컨트롤러 호출됨");
        log.info("OrderController -> autoOrder() 호출됨 -> 첫 번째 회원 데이터 가져올 예정");
        // 회원 1번의 데이터를 가져온다.
        Member member = memberRepository.findById(1L).orElse(null);

        log.info("첫 번째 회원 데이터 가져옴 -> 회원 데이터 잘 가져왔나 확인 예정");
        // 만약 회원이 없다면 다시 자동 회원가입하여 가져온다.
        if(member == null){
            log.info("회원 데이터 못가져옴(회원 테이블이 비어있음) -> 다시 회원가입 해서 회원 데이터 가져옴");
            member = memberService.autoJoin();
        }

        log.info("회원 데이터 잘 가져옴 -> 임시 결제 내역 객체와 주문 객체를 생성하고 DB에 저장 후 주문 객체만 가져오는 OrderService.autoOrder() 호출 예정");
        // "단건 결제 상품"이라는 상품을 주문하는 로직을 실행해서
        // 해당 주문에 대해 결재 내역(Payment) 테이블을 임시로 만들고 (아직 결제 전이라 임시로 미리 틀을 만들어 놓음)
        // 주문(order) 테이블을 생성하여 가져온다.
        Order order = orderService.autoOrder(member, "단건 결제 상품");

        log.info("OrderService.autoOrder() 호출 완료하여 주문 객체 가져옴 -> 주문 객체 만들어서 잘 가져왔는지 확인하여 메시지 작성 예정");
        // 주문(order) 테이블이 잘 만들어 졌으면 "주문 성공"을 아니면 "주문 실패"를 message에 담는다.
        String message = "주문 실패";
        if(order != null) {
            log.info("주문 객체가 존재함");
            message = "주문 성공";
            log.info("문자열 변수 메시지에 [주문 성공] 문자열 저장 완료");
        }

        log.info("메시지 변수의 값을 브라우저에 띄우기 위해 우선 UTF-8 문자셋으로 URL 인코딩 예정");
        // 해당 메세지를 화면에 띄우기 위해 해당 문자열을
        // UTF-8 문자셋으로 URL 인코딩
        String encode = URLEncoder.encode(message, StandardCharsets.UTF_8);

        log.info("메시지 인코딩 완료 -> 리다이렉트로 /order?message=인코딩한 메시지&merchantUid=주문객체에 있는 주문 번호 를 반환");
        // 리다리렉트로 다시 주문페이지로 이동하는데 파라미터로 message와 orderUid의 값을 같이 전달
        return "redirect:/order?message="+encode+"&merchantUid="+order.getMerchantUid();
    }
}
