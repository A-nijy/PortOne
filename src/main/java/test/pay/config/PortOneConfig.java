package test.pay.config;


import com.siot.IamportRestClient.IamportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


// 포트원의 키 값들을 세팅
// IamportClient객체를 내 가맹점으로 초기화

@Configuration
public class PortOneConfig {

    @Value("${imp.api.key}")
    private String apiKey;
    @Value("${imp.api.secret_key}")
    private String secretKey;

    @Bean
    public IamportClient iamportClient() {
        System.out.println("apiKey = " + apiKey);
        System.out.println("secretKey = " + secretKey);
        return new IamportClient(apiKey, secretKey);
    }
}
