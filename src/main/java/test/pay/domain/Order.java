package test.pay.domain;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import test.pay.enumeration.PaymentStatus;


@Entity
@Getter
@NoArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long price;                         // 금액
    private String itemName;                    // 상품 이름
    private String merchantUid;                 // 주문 번호
    private String impUid;                      // 결제 번호

    @Enumerated(EnumType.STRING)                // enum은 값이 순서 번호로 저장된다. 그러면 해당 enum에 값이 추가된다면 값이 변경된다. 이를 막기 위해 순서를 저장하는 것이 아닌 String으로 명칭을 저장한다.
    private PaymentStatus status;               // 결제 상태 (OK, READY, CANCEL)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Order(Long price, String itemName, String merchantUid, PaymentStatus status, Member member) {
        this.price = price;
        this.itemName = itemName;
        this.merchantUid = merchantUid;
        this.status = status;
        this.member = member;

    }

    public void updateBySuccess(String impUid) {
        this.status = PaymentStatus.OK;
        this.impUid = impUid;
    }
}
