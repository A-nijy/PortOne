package test.pay.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;                    // 성함
    private String email;                       // 이메일
    private String address;                     // 주소



    // 회원 생성
    @Builder
    public Member(String username, String email, String address) {
        this.username = username;
        this.email = email;
        this.address = address;
    }
}
