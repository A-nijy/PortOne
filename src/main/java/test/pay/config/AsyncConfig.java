package test.pay.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;


// 비동기 처리를 위한 설정

@Configuration
@EnableAsync        // spring에서 비동기 처리를 사용할 수 있도록 활성화
public class AsyncConfig {

    @Bean(name = "asyncExecutor1")                                                              // 빈 이름을 asyncExecutor1
    public Executor getAsyncExecutor1() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();                         // Spring이 제공하는 클래스로 스레드 풀을 구성하는 클래스
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());        // 스레드 풀에서 작업을 거부할 경우 현재 스레드에서 해당 작업을 처리하도록 지정
        executor.setCorePoolSize(10);                                                           // 스레드 풀의 기본 크기 = 10
        executor.setMaxPoolSize(100);                                                           // 스레드 풀의 최대 크기 = 100
        executor.setQueueCapacity(100);                                                         // 작업 큐의 용량 = 100 (큐가 가득 찬 경우에는 스레드 풀의 크기가 최대 크기까지 확장됨)
        executor.setThreadNamePrefix("MyAsyncThread1-");                                        // 스레드의 이름 접두사를 "MyAsyncThread1-"로 지정 (스레드 풀에서 생성되는 각 스레드의 이름에 사용됨)
        executor.initialize();                                                                  // 설정한 값들을 기반으로 ThreadPoolTaskExecutor 초기화
        return executor;
    }
}
