package test.pay.cancel;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.pay.dto.CancelTestDto;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CancelService {

    @Value("${imp.api.key}")
    private String apiKey;
    @Value("${imp.api.secret_key}")
    private String secretKey;


    // 토큰 발행
    public String getAccessToken() throws IOException {

        URL url = new URL("https://api.iamport.kr/users/getToken");
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        // 요청 방식을 Post 메서드로 설정
        conn.setRequestMethod("POST");

        // 요청의 Content-Type과 Accept 헤더 설정
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");

        // 해당 연결을 출력 스트림(요청)으로 사용
        conn.setDoOutput(true);

        // JSON 객체에 해당 API가 필요로하는 데이터 추가.
        JsonObject json = new JsonObject();
        json.addProperty("imp_key", apiKey);
        json.addProperty("imp_secret", secretKey);

        // 출력 스트림으로 해당 conn에 요청
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        bw.write(json.toString()); // json 객체를 문자열 형태로 HTTP 요청 본문에 추가
        bw.flush();                 // BufferedWriter 비우기
        bw.close();                 // BufferedWriter 종료

        // 입력 스트림으로 conn 요청에 대한 응답 반환
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        Gson gson = new Gson(); // 응답 데이터를 자바 객체로 변환
        String response = gson.fromJson(br.readLine(), Map.class).get("response").toString();
        String accessToken = gson.fromJson(response, Map.class).get("access_token").toString();
        br.close(); // BufferedReader 종료

        conn.disconnect(); // 연결 종료

        log.info("Iamport 엑세스 토큰 발급 성공 : {}", accessToken);
        log.info("Iamport response 발급 성공 : {}", response);

        return accessToken;
    }




    // 주문 취소
    public void refundRequest(CancelTestDto cancelTestDto) throws IOException {

        URL url = new URL("https://api.iamport.kr/payments/cancel");
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        // 요청 방식을 POST로 설정
        conn.setRequestMethod("POST");

        // 요청의 Content-Type, Accept, Authorization 헤더 설정
        conn.setRequestProperty("Content-type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization", cancelTestDto.getToken());

        // 해당 연결을 출력 스트림(요청)으로 사용
        conn.setDoOutput(true);

        // JSON 객체에 해당 API가 필요로하는 데이터 추가.
        JsonObject json = new JsonObject();
        json.addProperty("merchant_uid", cancelTestDto.getMerchantUid());
        json.addProperty("reason", cancelTestDto.getReason());

        // 출력 스트림으로 해당 conn에 요청
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        bw.write(json.toString());
        bw.flush();
        bw.close();

        // 입력 스트림으로 conn 요청에 대한 응답 반환
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        br.close();
        conn.disconnect();

        log.info("결제 취소 완료 : 주문 번호 {}", cancelTestDto.getMerchantUid());


//        // 위에 코드까지만 작성해도 환불은 잘 되었는데
//        // 블로그에서
//        //주의할점으로 처음에는 요청에 대한 응답이 성공적으로 반환되었지만 결제 완료 메세지는 오지만, 결제 취소 알림 메세지가 오질않았다.
//        //이유를 알아보니 해당 URL의 요청에 대한 응답 코드가 200번이더라도 응답 Body의 code 키 값이 0이 아닐 경우에는 환불에 실패한 것이라고 한다.
//        //이 경우에 원인을 찾기 위해서는 body의 message를 통해 확인할 수 있다.
//        //이 내용은 refundRequest() 메서드에 아래의 코드를 추가해 로그를 통해 확인해볼 수 있다.
//        //라고 해서 추가해보았다. (무슨 기능을 하고 있는지 확인해보고 싶어서)
//        // + 추가해보니 에러뜸 (입력 스트림이 이미 닫혀서 더 이상 읽을 수 없다.)
//        String responseJson = new BufferedReader(new InputStreamReader(conn.getInputStream()))
//                .lines()
//                .collect(Collectors.joining("\n"));
//
//        System.out.println("응답 본문: " + responseJson);
//
//        JsonObject jsonResponse = JsonParser.parseString(responseJson).getAsJsonObject();
//        String resultCode = jsonResponse.get("code").getAsString();
//        String resultMessage = jsonResponse.get("message").getAsString();
//
//        System.out.println("결과 코드 = " + resultCode);
//        System.out.println("결과 메시지 = " + resultMessage);
    }




}
