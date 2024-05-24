package com.mung.product.repository;

import com.mung.product.request.SearchProductCondition;
import com.mung.product.response.ProductSearchResponse;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;


import java.util.List;

import static com.mung.product.domain.QCategory.*;
import static com.mung.product.domain.QProduct.*;
import static com.mung.product.domain.QProductCategory.*;
import static com.querydsl.core.group.GroupBy.*;
import static org.springframework.util.StringUtils.hasText;

public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ProductRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, em);
    }

    @Override
    public Page<ProductSearchResponse> search(SearchProductCondition condition, Pageable pageable) {

        List<ProductSearchResponse> response = queryFactory
                .selectFrom(product)
                .leftJoin(product.categories, productCategory)
                .leftJoin(productCategory.id.category, category)
                .where(productIdEq(condition.getId()),
                        nameContains(condition.getName()),
                        compIdEq(condition.getCompId()),
                        categoryEq(condition.getCategoryIds())
                        )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(createOrderSpecifier(
                        condition.getSortBy(),
                        condition.getSortDirection()
                ))
                .transform(
                        groupBy(product.id).list(
                                Projections.constructor(ProductSearchResponse.class,
                                        product,
                                        list(category))
                        )
                );

        Long count = queryFactory
                .select(product.count())
                .from(product)
                .fetchOne();

        return new PageImpl<>(response, pageable, count);
    }

    private OrderSpecifier<?> createOrderSpecifier(String sortBy, String sortDirection) {
        OrderSpecifier<?> path = null;
        sortDirection = (sortDirection != null ? sortDirection : "asc");

        path = switch (hasText(sortBy) ? sortBy : "id") {
            case "id" -> (sortDirection.equals("desc") ? product.id.desc() : product.id.asc());
            case "name" -> (sortDirection.equals("desc") ? product.name.desc() : product.name.asc());
            default -> path;
        };

        return path;
    }

    private BooleanExpression categoryEq(List<Long> ids) {
        return (ids != null && !ids.isEmpty()) ? productCategory.id.category.id.in(ids) : null;
    }

    private BooleanExpression productIdEq(Long productId) {
        return productId != null ? product.id.eq(productId) : null;
    }

    private BooleanExpression nameContains(String name) {
        return hasText(name) ? product.name.contains(name) : null;
    }

    private BooleanExpression compIdEq(Long compId) {
        return compId != null ? product.compId.eq(compId) : null;
    }

}
