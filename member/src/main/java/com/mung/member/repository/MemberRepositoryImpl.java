package com.mung.member.repository;

import com.mung.member.domain.Role;
import com.mung.member.request.MemberSearchCondition;
import com.mung.member.response.MemberSearch;
import com.mung.member.response.QMemberSearch;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.mung.member.domain.QMember.*;
import static org.springframework.util.StringUtils.*;

public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<MemberSearch> search(MemberSearchCondition condition, Pageable pageable) {
        List<MemberSearch> members =  queryFactory.select(
                new QMemberSearch(
                        member.id,
                        member.name,
                        member.email,
                        member.tel,
                        member.role,
                        member.address,
                        member.loginFailCount,
                        member.isLocked))
                .from(member)
                .where(memberIdEq(condition.getMemberId()),
                        emailContains(condition.getEmail()),
                        telContains(condition.getTel()),
                        roleEq(condition.getRole()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(createOrderSpecifier(
                        condition.getSortBy(),
                        condition.getSortDirection()))
                .fetch();

        Long count = queryFactory
                .select(member.count())
                .from(member)
                .where()
                .fetchOne();

        return new PageImpl<>(members, pageable, count);
    }

    private OrderSpecifier<?> createOrderSpecifier(String sortBy, String sortDirection) {
        OrderSpecifier<?> path = null;
        sortDirection = (sortDirection != null ? sortDirection : "asc");

        path = switch (hasText(sortBy) ? sortBy : "id") {
            case "id" -> (sortDirection.equals("desc") ? member.id.desc() : member.id.asc());
            case "name" -> (sortDirection.equals("desc") ? member.name.desc() : member.name.asc());
            case "email" -> (sortDirection.equals("desc") ? member.email.desc() : member.email.asc());
            default -> path;
        };

        return path;
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return memberId != null ? member.id.eq(memberId) : null;
    }

    private BooleanExpression emailContains(String email) {
        return hasText(email) ? member.email.contains(email) : null;
    }

    private BooleanExpression telContains(String tel) {
        return hasText(tel) ? member.tel.contains(tel) : null;
    }

    private BooleanExpression roleEq(String str) {
        return hasText(str) ? member.role.eq(str.equals("user") ? Role.USER : str.equals("comp") ? Role.COMP : Role.ADMIN) : null;
    }
}
