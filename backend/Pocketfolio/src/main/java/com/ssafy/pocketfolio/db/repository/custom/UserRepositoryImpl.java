package com.ssafy.pocketfolio.db.repository.custom;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.pocketfolio.api.dto.response.SearchUserListRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.ssafy.pocketfolio.db.entity.QFollow.follow;
import static com.ssafy.pocketfolio.db.entity.QUser.user;

@Log4j2
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SearchUserListRes> searchUsers(Long myUserSeq, String keyword, Pageable pageable) {
        log.debug("UserRepositoryImpl: searchUsers()");

        List<SearchUserListRes> content = queryFactory
            .select(Projections.fields(SearchUserListRes.class,
                user.userSeq,
                user.name,
                user.profilePic,
                user.describe,
                ExpressionUtils.as(
                    JPAExpressions
                        .select(follow.count())
                        .from(follow)
                        .where(follow.userTo.userSeq.eq(user.userSeq)), "followerTotal"
                ),
                ExpressionUtils.as(
                    JPAExpressions
                        .select(follow.count())
                        .from(follow)
                        .where(follow.userFrom.userSeq.eq(user.userSeq)), "followingTotal"
                ),
                Expressions.cases()
                    .when(follow.userFrom.isNotNull()).then(true)
                    .otherwise(false).as("hasFollowed")
            ))
            .from(user)
            .leftJoin(follow).on(follow.userTo.userSeq.eq(user.userSeq).and(follow.userFrom.userSeq.eq(myUserSeq)))
            .where(user.name.contains(keyword).or(user.describe.contains(keyword)))
            .orderBy(getOrderSpecifiers(pageable))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(user.count())
            .from(user)
            .where(user.name.contains(keyword).or(user.describe.contains(keyword)));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private OrderSpecifier<?> getOrderSpecifiers(Pageable pageable) {
        NumberPath<Long> followerTotal = Expressions.numberPath(Long.class, "followerTotal"); // alias가 있을 때만 작동함

//        Sort orders = pageable.getSort();
//        if (!orders.isEmpty()) {
//            for (Sort.Order order : orders) {
//                switch (order.getProperty()) {
//                    default:
//                        return followerTotal.desc();
//                }
//            }
//        }
        return followerTotal.desc();
    }
}
