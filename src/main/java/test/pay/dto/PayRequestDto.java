package test.pay.dto;

import lombok.*;



// 뷰(View)로 전달할 결제 관련 데이터

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PayRequestDto {

    private String merchantUid;             // 주문 번호
    private String itemName;                // 상품 이름
    private Long paymentPrice;              // 결제 금액
    private String buyerName;               // 구매자 이름
    private String buyerEmail;              // 구매자 이메일
    private String buyerAddress;            // 구매자 주소
}
