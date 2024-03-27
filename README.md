# application.yml

<pre><code>

spring:
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:onedoller-shop
    username: sa
    password:
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: create
    properties:
      hibernate:
        format_sql: true
        default_batch_fetch_size: 1000
    open-in-view: false
  h2:
    console:
      enabled: true
      path: /h2-console



# 아임포트 가맹점 번호 / 키 / 비밀키
imp:
  code: 포트원 가맹점 번호
  api:
    key: 일반 키
    secret_key: 비밀키

</code></pre>