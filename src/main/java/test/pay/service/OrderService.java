package test.pay.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.pay.domain.Member;
import test.pay.domain.Order;
import test.pay.domain.repository.OrderRepository;
import test.pay.enumeration.PaymentStatus;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;


    // 특정 상품에 대한 주문 하기
    public Order autoOrder(Member member, String itemName) {

        log.info("OrderService -> autoOrder() 호출됨 -> 주문 객체 생성 예정 (아직 결제가 된 상태가 아니기에 impUid 제외)");
        // 주문 테이블 생성
        Order order = new Order(100L, itemName, UUID.randomUUID().toString(), PaymentStatus.READY, member);

        log.info("주문 객체 생성 완료 -> 주문 객체 DB에 저장 예정");
        orderRepository.save(order);

        log.info("주문 객체 DB에 저장 완료 -> 주문 객체 반환");
        return order;
    }
}
