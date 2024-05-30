package com.mung.product.repository;

import static com.mung.product.domain.QCategory.category;
import static com.mung.product.domain.QProduct.product;
import static com.querydsl.core.group.GroupBy.groupBy;
import static org.springframework.util.StringUtils.hasText;

import com.mung.product.dto.ProductDto.ProductSearchResponse;
import com.mung.product.dto.ProductDto.SearchProductCondition;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ProductRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, em);
    }

    @Override
    public Page<ProductSearchResponse> search(SearchProductCondition condition, Pageable pageable) {

        List<ProductSearchResponse> response = queryFactory
            .selectFrom(product)
            .join(product.category, category).fetchJoin()
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
                    Projections.constructor(ProductSearchResponse.class, product, category)
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
            case "name" ->
                (sortDirection.equals("desc") ? product.name.desc() : product.name.asc());
            default -> path;
        };

        return path;
    }

    private BooleanExpression categoryEq(List<Long> ids) {
        return (ids != null && !ids.isEmpty()) ? product.category.id.in(ids) : null;
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
