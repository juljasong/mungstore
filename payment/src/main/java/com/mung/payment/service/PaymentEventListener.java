package com.mung.payment.service;

import com.mung.order.domain.Orders;
import com.mung.order.service.OrderService;
import com.mung.payment.domain.DecreaseStockEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final OrderService orderService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decreaseStock(DecreaseStockEvent event) {
        Orders order = orderService.getOrder(event.getOrderId(), event.getMemberId());
        orderService.decreaseStock(order);
        log.info("PaymentService.completeKakaoPayment :: 주문번호 {} :: 재고 차감 이벤트 처리 완료",
            event.getOrderId());
        // FIXME 재고 부족 일 때 .. 바로 응답이 가는게 좋으니 동기 처리가 나을 것 같다.(연습 겸 남겨둠)
    }

}
