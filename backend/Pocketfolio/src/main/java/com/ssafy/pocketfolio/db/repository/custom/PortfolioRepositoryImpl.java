package com.ssafy.pocketfolio.db.repository.custom;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.pocketfolio.api.dto.response.SearchPortfolioListRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.ssafy.pocketfolio.db.entity.QArrange.arrange;
import static com.ssafy.pocketfolio.db.entity.QFollow.follow;
import static com.ssafy.pocketfolio.db.entity.QPortfolio.portfolio;
import static com.ssafy.pocketfolio.db.entity.QRoom.room;
import static com.ssafy.pocketfolio.db.entity.QRoomHit.roomHit;
import static com.ssafy.pocketfolio.db.entity.QRoomLike.roomLike;
import static com.ssafy.pocketfolio.db.entity.QTag.tag;
import static com.ssafy.pocketfolio.db.entity.QUser.user;

@Log4j2
@RequiredArgsConstructor
public class PortfolioRepositoryImpl implements PortfolioRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SearchPortfolioListRes> searchPortfolios(String keyword, Pageable pageable) {
        log.debug("PortfolioRepositoryImpl: searchPortfolios()");

        List<SearchPortfolioListRes> content = queryFactory
            .select(Projections.fields(SearchPortfolioListRes.class,
                portfolio.portSeq,
                portfolio.name,
                user.userSeq,
                user.name.as("userName"),
                user.profilePic.as("userProfilePic"),
                Expressions.stringTemplate("group_concat({0})", tag.name).as("tagsString"),
                room.roomSeq,
                room.name.as("roomName"),
                room.thumbnail.as("roomThumbnail"),
                Expressions.cases()
                    .when(room.isNotNull()).then(JPAExpressions
                        .select(roomLike.count())
                        .from(roomLike)
                        .where(roomLike.room.roomSeq.eq(room.roomSeq)))
                    .otherwise((Long) null).as("like"), // case when을 안 쓰면 null이 아닌 0이 들어감
                Expressions.cases()
                    .when(room.isNotNull()).then(JPAExpressions
                        .select(roomHit.count())
                        .from(roomHit)
                        .where(roomHit.room.roomSeq.eq(room.roomSeq)))
                    .otherwise((Long) null).as("hit"),
                ExpressionUtils.as(
                    JPAExpressions
                        .select(follow.count())
                        .from(follow)
                        .where(follow.userTo.userSeq.eq(user.userSeq)), "follower"
                )
            ))
            .from(portfolio)
            .join(user).on(user.userSeq.eq(portfolio.user.userSeq))
            .leftJoin(arrange).on(arrange.portfolio.portSeq.eq(portfolio.portSeq))
            .leftJoin(room).on(room.roomSeq.eq(arrange.room.roomSeq))
            .leftJoin(tag).on(tag.portfolio.portSeq.eq(portfolio.portSeq))
            .where(portfolio.name.contains(keyword).or(portfolio.summary.contains(keyword)).or(user.name.contains(keyword)))
            .groupBy(portfolio.portSeq)
            .orderBy(getOrderSpecifiers(pageable))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(portfolio.count())
            .from(portfolio)
            .where(portfolio.name.contains(keyword).or(portfolio.summary.contains(keyword)).or(user.name.contains(keyword)));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private OrderSpecifier<?> getOrderSpecifiers(Pageable pageable) {
        NumberPath<Long> follower = Expressions.numberPath(Long.class, "follower"); // alias가 있을 때만 작동함

        Sort orders = pageable.getSort();
        if (!orders.isEmpty()) {
            for (Sort.Order order : orders) {
                if (order.getProperty().equals("follower")) {
                    return follower.desc();
                }
                return portfolio.updated.desc();
            }
        }
        return portfolio.updated.desc();
    }
}
