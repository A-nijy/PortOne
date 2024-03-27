package test.pay.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import test.pay.dto.PayRequestDto;
import test.pay.dto.PortOneCallbackRequestDto;
import test.pay.service.PortOneService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PortOneController {

    private final PortOneService portOneService;
    private final ObjectMapper objectMapper;


    // 주문 번호(merchantUid)를 가지고 해당 주문에 대한 결제 정보를 만들어서 결제 페이지로 이동 (결제 페이지 요청)
    // 즉, 해당 주문을 결제하기 위한 정보를 가지고 결제 페이지로 이동 (아직 KG 이니시스 결제창 여는 것 아님/ 대신 해당 결제창을 여는 버튼이 있음 누르면 결제창 열어짐)
    @GetMapping("/payment/{merchantUid}")
    public String paymentPage(@PathVariable(name = "merchantUid", required = false) String merchantUid, Model model){

        log.info("Get 메서드로 /payment/{merchantUid} 주소에 접근하여 컨트롤러 호출됨");
        log.info("PortOneController -> paymentPage() 호출됨 -> 호출하면서 보내준 merchantUid(주문 번호)를 가지고 결제 정보 DTO를 만들어서 반환하는 PortOneService.createPayRequestDto() 호출 예정");
        // 주문 번호를 가지고 해당 결제 정보 만들어서 가져오기
        PayRequestDto payRequestDto = portOneService.createPayRequestDto(merchantUid);

        log.info("PortOneService.createPayRequest()를 통해 결제 정보 DTO인 PayRequestDto 객체 가져옴 -> PayRequestDto를 클라이언트에게 전달하기 위해 모델에 담을 예정");
        // 결제 정보를 뷰에 전달하는 데이터에 담기
        model.addAttribute("requestDto", payRequestDto);

        log.info("PayRequestDto 객체를 모델에 담음 -> payment.html 반환");
        // 결제 페이지로 이동
        return "payment";
    }



    //==============================================================================
    // 단건 결제를 하는 KG이니시스
    //==============================================================================


    // 포트원에 결제 요청을 보내서 결제 요청 성공시 Callback 함수를 통해 호출되는 api  (KG이니시스로 결제를 성공하면 호출되는 api)
    // 결제 고유 번호(imp_uid)와 가맹점 주문번호(merchant_uid)를 수신 (request)
//    @ResponseBody
//    @PostMapping("/payment")
//    public ResponseEntity<IamportResponse<Payment>> validationPayment(@RequestBody String request) throws JsonProcessingException {
//
//        log.info("Post 메서드로 /payment 주소에 접근하여 컨트롤러 호출됨");
//        log.info("PostOneController -> validationPayment() 호출됨 -> 호출하면서 받아온 request(imp_uid와 merchant_uid)를 가지고 PortOneCallbackRequestDto 객체 생성 예정");
//        // 결제 성공시에 반환되는 데이터 request(payType, paymentUid, orderUid)를 가지고 paymentCallbackRequestDto객체를 생성
//        // fromString()은 보통 JSON을 파싱해서 DTO객체로 변환해준다.
//        PortOneCallbackRequestDto portOneCallbackRequestDto = PortOneCallbackRequestDto.fromString(request);
//
//        log.info("imp_uid와 merchant_uid의 값만 정의된 PortOneCallbackRequestDto 객체 생성 완료 -> PortOneCallbackRequestDto에 저장된 데이터를 가지고 결제 정보를 검증하는 PortOneService.validatePayment()를 호출하여 아임포트에서 발생한 결제에 대한 응답 객체를 가져올 예정");
//        // 결제 정보를 검증
//        IamportResponse<Payment> iamportResponse = portOneService.validatePayment(portOneCallbackRequestDto);
//
//
//        log.info("PortOneService.validatePayment()를 통해 검증 완료 -> 아임포트에서 받아온 결제 정보 객체와 함께 상태코드 200 반환");
//        return new ResponseEntity<>(iamportResponse, HttpStatus.OK);
//    }

    @ResponseBody
    @PostMapping("/payment")
    public ResponseEntity<IamportResponse<Payment>> validationPayment(@RequestBody PortOneCallbackRequestDto request) throws JsonProcessingException {

        log.info("Post 메서드로 /payment 주소에 접근하여 컨트롤러 호출됨");

        log.info("imp_uid와 merchant_uid의 값만 정의된 PortOneCallbackRequestDto 객체 생성 완료 -> PortOneCallbackRequestDto에 저장된 데이터를 가지고 결제 정보를 검증하는 PortOneService.validatePayment()를 호출하여 아임포트에서 발생한 결제에 대한 응답 객체를 가져올 예정");
        // 결제 정보를 검증
        IamportResponse<Payment> iamportResponse = portOneService.validatePayment(request);


        log.info("PortOneService.validatePayment()를 통해 검증 완료 -> 아임포트에서 받아온 결제 정보 객체와 함께 상태코드 200 반환");
        return new ResponseEntity<>(iamportResponse, HttpStatus.OK);
    }



    @GetMapping("/success-payment")
    public String successPaymentPage() {

        log.info("Get 메서드로 /success-payment 주소에 접근하여 클라이언트 호출");
        log.info("success-payment.html 반환");
        return "success-payment";
    }

    @GetMapping("/fail-payment")
    public String failPaymentPage() {

        log.info("Get 메서드로 /fail-payment 주소에 접근하여 클라이언트 호출");
        log.info("fail-payment.html 반환");
        return "fail-payment";
    }

}
