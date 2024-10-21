package com.ssafy.pocketfolio.db.repository.custom;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.pocketfolio.api.dto.response.SearchRoomListRes;
import com.ssafy.pocketfolio.db.entity.QRoomLike;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.ssafy.pocketfolio.db.entity.QCategory.category;
import static com.ssafy.pocketfolio.db.entity.QFollow.follow;
import static com.ssafy.pocketfolio.db.entity.QRoom.room;
import static com.ssafy.pocketfolio.db.entity.QRoomCategory.roomCategory;
import static com.ssafy.pocketfolio.db.entity.QRoomHit.roomHit;
import static com.ssafy.pocketfolio.db.entity.QRoomLike.roomLike;
import static com.ssafy.pocketfolio.db.entity.QUser.user;

@Log4j2
@RequiredArgsConstructor
public class RoomRepositoryImpl implements RoomRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SearchRoomListRes> searchRooms(Long myUserSeq, String keyword, List<Long> categories, Pageable pageable) {
        log.debug("RoomRepositoryImpl: searchRooms()");

        QRoomLike roomLike2 = new QRoomLike("roomLike2"); // TODO: QEntity 생성 중복 코드 private으로 넘겨도 되는지 확인

//        NumberPath<Long> sortAlias = Expressions.numberPath(Long.class, "like");
//        Sort orders = pageable.getSort();
//        if (!orders.isEmpty()) {
//            for (Sort.Order order : orders) {
//                switch (order.getProperty()) {
//                    case "hit":
//                        sortAlias = Expressions.numberPath(Long.class, "hit");
//                        break;
//                    case "follower":
//                        sortAlias = Expressions.numberPath(Long.class, "follower");
//                        break;
//                    default:
//                        sortAlias = Expressions.numberPath(Long.class, "like");
//                }
//            }
//        }

        List<SearchRoomListRes> content = queryFactory
            .select(Projections.fields(SearchRoomListRes.class,
                room.roomSeq,
                room.thumbnail,
                room.name,
                user.userSeq,
                user.name.as("userName"),
                user.profilePic.as("userProfilePic"),
                ExpressionUtils.as(
                    JPAExpressions
                        .select(roomLike.count())
                        .from(roomLike)
                        .where(roomLike.room.roomSeq.eq(room.roomSeq)), "like"
                ),
                ExpressionUtils.as(
                    JPAExpressions
                        .select(roomHit.count())
                        .from(roomHit)
                        .where(roomHit.room.roomSeq.eq(room.roomSeq)), "hit"
                ),
//                Expressions.numberTemplate(Long.class, "IFNULL({0}, 0)",
//                    JPAExpressions
//                        .select(roomHit.count())
//                        .from(roomHit)
//                        .where(roomHit.room.roomSeq.eq(room.roomSeq))
//                ).as("hit"),
                Expressions.cases()
                    .when(roomLike2.user.userSeq.isNotNull()).then(true)
                    .otherwise(false).as("isLiked"),
                Expressions.stringTemplate("group_concat({0})", category.name).as("categoryName"),
                ExpressionUtils.as(
                    JPAExpressions
                        .select(follow.count())
                        .from(follow)
                        .where(follow.userFrom.userSeq.eq(user.userSeq)), "follower"
                )
            ))
            .from(room)
            .join(user).on(user.userSeq.eq(room.user.userSeq))
            .leftJoin(roomLike2).on(roomLike2.room.roomSeq.eq(room.roomSeq).and(roomLike2.user.userSeq.eq(myUserSeq)))
            .leftJoin(roomCategory).on(roomCategory.room.roomSeq.eq(room.roomSeq))
            .leftJoin(category).on(category.categorySeq.eq(roomCategory.category.categorySeq))
            .where(getBooleanBuilder(keyword, categories))
            .groupBy(room.roomSeq)
//            .orderBy(sortAlias.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(room.count())
            .from(room)
            .where((room.name.contains(keyword).or(user.name.contains(keyword)))
                .and(category.categorySeq.in(categories)));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanBuilder getBooleanBuilder(String keyword, List<Long> categories) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(room.name.contains(keyword).or(user.name.contains(keyword)));

        if (categories != null && !categories.isEmpty() && categories.size() < 11) {
            builder.and(category.categorySeq.in(categories));
        }
        return builder;
    }

    private OrderSpecifier<?> getOrderSpecifiers(Pageable pageable) {
        NumberPath<Long> like = Expressions.numberPath(Long.class, "like");
        NumberPath<Long> hit = Expressions.numberPath(Long.class, "hit");
        NumberPath<Long> follower = Expressions.numberPath(Long.class, "follower");

        Sort orders = pageable.getSort();
        if (orders.isEmpty()) {
            return like.desc();
        }

        for (Sort.Order order : orders) {
            switch (order.getProperty()) {
                case "hit":
                    return hit.desc();
                case "follower":
                    return follower.desc();
                default:
                    return like.desc();
            }
        }
        return like.desc();
    }
}
