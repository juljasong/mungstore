package com.mung.order.repository;

import static com.mung.order.domain.QOrders.orders;

import com.mung.order.dto.OrderDto.GetOrdersResponse;
import com.mung.order.dto.OrderDto.OrderSearchRequest;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public OrderRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, em);
    }

    @Override
    public Page<GetOrdersResponse> search(OrderSearchRequest condition, Pageable pageable) {
        List<GetOrdersResponse> response = queryFactory
            .select(Projections.constructor(GetOrdersResponse.class,
                orders.id,
                orders.status,
                orders.totalPrice,
                orders.createdAt))
            .from(orders)
            .limit(condition.getPageSize())
            .offset(condition.getPageNumber())
            .where(createdAtFrom(condition.getOrderedAtFrom()),
                createdAtTo(condition.getOrderedAtTo())
            )
            .fetch();

        Long count = queryFactory
            .select(orders.count())
            .from(orders)
            .fetchOne();

        return new PageImpl<>(response, pageable, count);
    }

    private BooleanExpression createdAtFrom(LocalDateTime orderedAtFrom) {
        return orderedAtFrom != null ? orders.createdAt.after(orderedAtFrom) : null;
    }

    private BooleanExpression createdAtTo(LocalDateTime orderedAtTo) {
        return orderedAtTo != null ? orders.createdAt.before(orderedAtTo) : null;
    }

}
