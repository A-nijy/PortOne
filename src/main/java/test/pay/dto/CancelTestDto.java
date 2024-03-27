package test.pay.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CancelTestDto {

    private String token;           // 에세스 토큰
    private String merchantUid;     // 주문 고유 번호
    private String reason;          // 환불 or 취소 사유
}
