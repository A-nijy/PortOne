package test.pay.service;


import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.pay.domain.Order;
import test.pay.domain.repository.OrderRepository;
import test.pay.dto.PayRequestDto;
import test.pay.dto.PortOneCallbackRequestDto;
import test.pay.enumeration.PaymentStatus;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Objects;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PortOneService {

    private final OrderRepository orderRepository;
    private final IamportClient iamportClient;


    // 주문 번호(merchantUid)를 가지고 해당 주문 테이블 데이터를 가져와서
    // 결제 정보(PayRequestDto) 데이터를 구성해서
    // 해당 결제 정보 데이터를 클라이언트(view)에게 전달하기 위해 생성
    public PayRequestDto createPayRequestDto(String merchantUid) {

        log.info("PortOneService -> createPayRequestDto() 호출됨 -> 받아온 merchantUid(주문번호)를 가지고 해당 주문번호인 주문 객체 DB에서 가져올 예정");
        // 주문 번호로 해당 주문 데이터 가져오기
        Order order = orderRepository.findByMerchantUid(merchantUid).orElseThrow(NoSuchElementException::new);

        log.info("merchantUid(주문번호)를 가지고 DB에서 해당 주문 객체 가져옴 -> 주문 객체에 있는 데이터들을 가지고 클라이언트에게 전달할 결제 정보 DTO인 PayRequestDto 객체를 생성 예정");
        // 가져온 주문 데이터를 가지고 클라이언트에게 전달할 결제 정보(RequestPayDto) 생성하기
        PayRequestDto payRequestDto = PayRequestDto.builder()
                .merchantUid(order.getMerchantUid())
                .itemName(order.getItemName())
                .buyerName(order.getMember().getUsername())
                .paymentPrice(order.getPrice())
                .buyerEmail(order.getMember().getEmail())
                .buyerAddress(order.getMember().getAddress())
                .build();

        log.info("클라이언트에게 전달할 결제 정보 DTO인 PayRequestDto 객체 생성 완료 -> PayRequestDto 반환");
        return payRequestDto;
    }



    // 결제 정보를 검증 하고 결제 상태를 결제 완료로 변경해준다.
    // 해당 메서드는 결제 요청 후 검증용 웹훅으로만 호출하자.
    public IamportResponse<Payment> validatePayment(PortOneCallbackRequestDto portOneCallbackRequestDto) {

        log.info("PortOneService -> validatePayment() 호출됨 -> 호출하면서 받은 PortOneCallbackRequestDto를 가지고 PortOneService.getIamportResponse()를 호출하여 아임포트로부터 결제 데이터 가져올 예정");

        try{
            log.info("PortOneCallbackRequestDto에 있는 impUid를 가지고 PortOneService.getIamportResponse() 호출 하여 아임포트에서 발생한 결제에 대한 응답 객체(결제 관련 정보)를 가져올 에정");
            // 결제 단건 조회
            IamportResponse<Payment> iamportResponse = getIamportResponse(portOneCallbackRequestDto);

            log.info("PortOneService.getIamportResponse()를 통해 impUid의 값으로 결제된 정보 객체를 아임포트로 부터 받아와서 IamportResponse<Payment> iamportResponse에 가져옴 -> PortOneCallbackRequestDto에 있는 merchantUid를 가지고 해당 주문 객체를 DB로 부터 가져올 예정");
            // 해당 주문 테이블 테이블를 가져온다.
            Order order = orderRepository.findByMerchantUid(portOneCallbackRequestDto.getMerchantUid()).orElseThrow(NoSuchElementException::new);

            log.info("merchantUid를 가지고 DB로 부터 주문 객체 가져옴 -> PortOneService.validatePaymentStatusAndPay()를 호출하여 DB에 있는 주문 객체와 아임포트에서 실제 결제된 정보 객체를 가지고 검증할 예정");
            // 포트원으로부터 받은 결제 데이터 iamportResponse와 주문 데이터를 가져와서 결제에 대해 검증한다.
            validatePaymentStatusAndPay(iamportResponse, order);

            // 만약 결제 상태가 OK라면?(결제가 완료된 상태라면)
            if(PaymentStatus.OK.equals(order.getStatus())){

                log.info("이미 결제 완료된 주문입니다. imp_uid={}, merchant_uid={}", order.getImpUid(), order.getMerchantUid());

            } else {

                // 기존 portOne객체(결제 정보)에 내용을 작성(수정)
                order.updateBySuccess(
                        iamportResponse.getResponse().getImpUid()
                );

                log.info("결제 완료 확인!, payment_uid={}, order_uid={}",
                        order.getImpUid(), order.getMerchantUid());
            }

            return iamportResponse;

        } catch (IamportResponseException | IOException e){
            throw new RuntimeException(e);
        }
    }




    //==============================================================================================================


    // 아임포트에서 제공하는 iamportClient를 이용해서 IamportResponse타입인 아임포트 payment 객체 생성
    // 즉, 아임포트로부터 받은 결제 데이터를 가져온다.
    private IamportResponse<Payment> getIamportResponse(PortOneCallbackRequestDto request) throws IamportResponseException, IOException {

        log.info("PortOneService -> getIamportResponse() 호출됨 -> 호출될 때 가져온 PortOneCallbackRequestDto에 있는 impUid(결제 고유 번호)를 가지고 아임포트에서 제공하는 IamportClient를 이용해서 아임포트로 부터 해당 impUid로 결제된 정보를 가져올 예정");
        IamportResponse<Payment> iamportResponse = iamportClient.paymentByImpUid(request.getImpUid());

        log.info("아임포트로 부터 impUid로 결제한 정보 내역을 IamportResponse<Payment> iamportResponse에 가져옴 -> iamportResponse 반환");
        return iamportResponse;
    }


    // 포트원으로부터 받은 결제 데이터를 가지고 검증하는 메서드
    // 결제 완료가 되지 않음 -> 주문 정보 및 결제 정보 삭제
    // 결제 완료는 되었으나 결제 금액이 다름 -> 주문 정보 및 결제 정보 삭제 후, 포트원에 결제 취소 요청
    private void validatePaymentStatusAndPay(IamportResponse<Payment> iamportResponse, Order order) throws IamportResponseException, IOException{

        log.info("PortOneService -> validatePaymentStatusAndPay() 호출됨 -> 우선 결제가 완료 되었는지 판별할 예정");
        log.info("결제가 완료되었는지 확인하기 위해 아임포트에서 가져온 결제 정보에 있는 결제 상태가 결제 완료 상태인지(결제 상태가 paid인지) 확인 예정");
        // 결제 완료 상태가 아니라면
        if(!iamportResponse.getResponse().getStatus().equals("paid")){

            log.info("결제가 완료되지 않음 -> 아직 결제가 완료되지 않았으면 비정상으로 판단 -> 해당 주문 객체를 DB에서 삭제 예정");
            // 해당 주문 정보 삭제
            orderRepository.delete(order);

            log.info("해당 주문 객체 삭제 완료 -> 결제 미완료 예외 발생");
            throw new RuntimeException("결제 미완료");
        }

        log.info("결제 완료 확인 완료 -> 주문 객체에 있는 결제할 금액 가져올 예정");
        // 주문 정보에 있는 가격
        Long price = order.getPrice();
        log.info("주문 객체에 있는 결제할 금액 가져옴 -> 아임포트에서 가져온 결제 정보를 통해 결제된 금액 가져올 예정");
        // 포트원에 결제된 가격
        Long portOnePrice = iamportResponse.getResponse().getAmount().longValue();

        log.info("결제된 금액 가져옴 -> 결제 예정 금액과 결제된 금액이 동일한지 판별 예정");
        // 주문 금액과 결제 금액이 동일한지 검증 (만약 다르다면)
        if(!Objects.equals(portOnePrice, price)){

            log.info("결제 예정 금액과 결제된 금액이 다름 -> 해당 주문 객체 DB에서 삭제 예정");
            // 해당 주문 정보 삭제
            orderRepository.delete(order);

            log.info("해당 주문 객체 DB에서 삭제 완료 -> 아임포트에게 결제를 취소 요청하기 위해 아임포트에서 결제 취소를 하기위해 제공하는 CancelData객체를 생성 예정");
            // 아임포트에게 결제를 취소를 요청하기 위해 결제 취소에 필요한 데이터를 포함하는 CancelData 객체를 생성한다.
            // 결제 고유 번호, 부분 취소 여부, 취소할 금액(결제된 가격)
            CancelData cancelData = new CancelData(iamportResponse.getResponse().getImpUid(), true, new BigDecimal(portOnePrice));

            log.info("아임포트에서 결제 취소하기 위해 제공하는 CancelData객체 생성 완료 -> 결제 취소를 요청하기 위해 아임포트에게 CancelData객체를 전달 예정");
            // 결제 서비스 클라이언트(iamportClient)를 이용해서 결제 고유 번호를 기반으로 결제를 취소하는데 이때 취소에 필요한 정보가 있는 CancelDate 객체를 제공
            iamportClient.cancelPaymentByImpUid(cancelData);

            log.info("아임포트에게 결제 취소 요청 완료 -> 잘못된 금액으로 결제되었음으로 예외 발생");
            throw new RuntimeException("결제금액 상이하여 취소, 클라이언트 측의 위변조 가능성 있음");
        }

        log.info("결제 예정 금액과 결제한 금액이 동일함 -> 결제 완료 판단과 결제 금액이 정확한지 검증 완료");
    }

}
