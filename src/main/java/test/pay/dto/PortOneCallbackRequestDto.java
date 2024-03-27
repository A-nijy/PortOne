package test.pay.dto;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.*;


// 결제가 성공적으로 끝나면 클라이언트로 부터 impUid와 merchantUid를 받아와서 만든다.

// = 결제가 정상적으로 완료되면 클라이언트로 부터 impUid와 merchantUid를 서버로 가져오는 용도
// + 가져온 ImpUid로 아임포트 결제 데이터를 가져오고 / merchantUid로 order데이터를 가져오기 위한 용도
// + 결제가 성공적으로 끝나야 impUid를 받는데 클라이언트로 부터 가져와서 order에 저장

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@ToString
public class PortOneCallbackRequestDto {

    private String impUid;          // 결제 고유 번호 (imp_uid)
    private String merchantUid;     // 주문 고유 번호 (merchant_uid)




    // JSON 문자열을 받아와서 파싱하여 PaymentCallbackRequestDto 객체로 변환
    public static PortOneCallbackRequestDto fromString(String request) {
        JsonObject requestJson = JsonParser.parseString(request).getAsJsonObject();
        String impUid = requestJson.getAsJsonPrimitive("imp_uid") == null ? null : requestJson.getAsJsonPrimitive("imp_uid").getAsString();
        String merchantUid = requestJson.getAsJsonPrimitive("merchant_uid") == null ? null : requestJson.getAsJsonPrimitive("merchant_uid").getAsString();

        return PortOneCallbackRequestDto.builder()
                .impUid(impUid)
                .merchantUid(merchantUid)
                .build();
    }
}
