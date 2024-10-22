package com.ssafy.pocketfolio.db.repository;

import com.ssafy.pocketfolio.db.entity.Room;
import com.ssafy.pocketfolio.db.view.SearchPortfolioListAddedRoomView;
import com.ssafy.pocketfolio.db.view.SearchPortfolioListView;
import com.ssafy.pocketfolio.db.view.SearchRoomListView;
import com.ssafy.pocketfolio.db.view.SearchUserListView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

// QueryDSL로 리팩토링 예정
public interface SearchRepository extends JpaRepository<Room, Long> {
    // 포켓 검색: 시작
//    @Query(value = "select r.room_seq as roomSeq, r.thumbnail, r.name, u.user_seq as userSeq, u.name as userName, u.profile_pic as userProfilePic, " +
//            "(select count(*) from room_like where room_seq = roomSeq) as `like`, " +
//            "(select count(*) from room_hit where room_seq = roomSeq) as hit, " +
//            "(select count(*) from room_like where room_seq = roomSeq and user_seq = :myUserSeq) as isLiked, " +
//            "group_concat(c.name) as categoryName " +
//            "from room r inner join user u on r.user_seq = u.user_seq left outer join room_category rc on r.room_seq = rc.room_seq " +
//            "left outer join category c on rc.category_seq = c.category_seq " +
//            "where r.name like %:keyword% and c.category_seq in :categories group by r.room_seq " +
//            "order by `like` desc",
//            countQuery = "select count(distinct(r.room_seq)) " +
//                    "from room r inner join user u on r.user_seq = u.user_seq left outer join room_category rc on r.room_seq = rc.room_seq " +
//                    "left outer join category c on rc.category_seq = c.category_seq " +
//                    "where r.name like %:keyword% and c.category_seq in :categories and :myUserSeq = :myUserSeq",
//            nativeQuery = true)
//    Page<SearchRoomListView> searchRoomOrderByLike(@Param("myUserSeq") Long myUserSeq, @Param("keyword") String keyword, @Param("categories") List<Long> categories, Pageable pageable);
//
//    @Query(value = "select r.room_seq as roomSeq, r.thumbnail, r.name, u.user_seq as userSeq, u.name as userName, u.profile_pic as userProfilePic, " +
//            "(select count(*) from room_like where room_seq = roomSeq) as `like`, " +
//            "(select count(*) from room_hit where room_seq = roomSeq) as hit, " +
//            "(select count(*) from room_like where room_seq = roomSeq and user_seq = :myUserSeq) as isLiked, " +
//            "group_concat(c.name) as categoryName " +
//            "from room r inner join user u on r.user_seq = u.user_seq left outer join room_category rc on r.room_seq = rc.room_seq " +
//            "left outer join category c on rc.category_seq = c.category_seq " +
//            "where r.name like %:keyword% and c.category_seq in :categories group by r.room_seq " +
//            "order by hit desc",
//            countQuery = "select count(distinct(r.room_seq)) " +
//                    "from room r inner join user u on r.user_seq = u.user_seq left outer join room_category rc on r.room_seq = rc.room_seq " +
//                    "left outer join category c on rc.category_seq = c.category_seq " +
//                    "where r.name like %:keyword% and c.category_seq in :categories and :myUserSeq = :myUserSeq",
//            nativeQuery = true)
//    Page<SearchRoomListView> searchRoomOrderByHit(@Param("myUserSeq") Long myUserSeq, @Param("keyword") String keyword, @Param("categories") List<Long> categories, Pageable pageable);
//
//    @Query(value = "select r.room_seq as roomSeq, r.thumbnail, r.name, u.user_seq as userSeq, u.name as userName, u.profile_pic as userProfilePic, " +
//            "(select count(*) from room_like where room_seq = roomSeq) as `like`, " +
//            "(select count(*) from room_hit where room_seq = roomSeq) as hit, " +
//            "(select count(*) from room_like where room_seq = roomSeq and user_seq = :myUserSeq) as isLiked, " +
//            "(select count(*) from follow where user_to = u.user_seq) as followerTotal, " +
//            "group_concat(c.name) as categoryName " +
//            "from room r inner join user u on r.user_seq = u.user_seq left outer join room_category rc on r.room_seq = rc.room_seq " +
//            "left outer join category c on rc.category_seq = c.category_seq " +
//            "where r.name like %:keyword% and c.category_seq in :categories group by r.room_seq " +
//            "order by followerTotal desc",
//            countQuery = "select count(distinct(r.room_seq)) " +
//                    "from room r inner join user u on r.user_seq = u.user_seq left outer join room_category rc on r.room_seq = rc.room_seq " +
//                    "left outer join category c on rc.category_seq = c.category_seq " +
//                    "where r.name like %:keyword% and c.category_seq in :categories and :myUserSeq = :myUserSeq",
//            nativeQuery = true)
//    Page<SearchRoomListView> searchRoomOrderByFollowerTotal(@Param("myUserSeq") Long myUserSeq, @Param("keyword") String keyword, @Param("categories") List<Long> categories, Pageable pageable);
    // 포켓 검색: 끝

    // 포트폴리오 검색: 시작
    @Query(value = "select p.port_seq as portSeq, p.name, " +
            "u.user_seq as userSeq, u.name as userName, u.profile_pic as userProfilePic, " +
            "group_concat(distinct t.name) as tags, max(a.arrange_seq) as arrangeSeq " +
            "from portfolio p inner join user u on p.user_seq = u.user_seq " +
            "left outer join arrange a on p.port_seq = a.port_seq " +
            "left outer join tag t on a.port_seq = t.port_seq " +
            "where p.name like %:keyword% or p.summary like %:keyword% group by p.port_seq " +
            "order by p.updated desc",
            countQuery = "select count(*) from portfolio p where p.name like %:keyword% or p.summary like %:keyword%",
            nativeQuery = true)
    Page<SearchPortfolioListView> searchPortfolioOrderByUpdated(String keyword, Pageable pageable);

    @Query(value = "select p.port_seq as portSeq, p.name, " +
            "u.user_seq as userSeq, u.name as userName, u.profile_pic as userProfilePic, " +
            "(select count(*) from follow where user_to = u.user_seq) as followerTotal, " +
            "group_concat(distinct t.name) as tags, max(a.arrange_seq) as arrangeSeq " +
            "from portfolio p inner join user u on p.user_seq = u.user_seq " +
            "left outer join arrange a on p.port_seq = a.port_seq " +
            "left outer join tag t on a.port_seq = t.port_seq " +
            "where p.name like %:keyword% or p.summary like %:keyword% group by p.port_seq " +
            "order by followerTotal desc",
            countQuery = "select count(*) from portfolio p where p.name like %:keyword% or p.summary like %:keyword%",
            nativeQuery = true)
    Page<SearchPortfolioListView> searchPortfolioOrderByFollowerTotal(String keyword, Pageable pageable);

    @Query(value = "select r.room_seq as roomSeq, r.name, r.thumbnail, " +
            "(select count(*) from room_like where room_seq = roomSeq) as `like`, " +
            "(select count(*) from room_hit where room_seq = roomSeq) as hit " +
            "from arrange a inner join room r on a.room_seq = r.room_seq " +
            "where a.arrange_seq = :arrangeSeq", nativeQuery = true)
    Optional<SearchPortfolioListAddedRoomView> searchPortfolioAddedRoom(Long arrangeSeq);
    // 포트폴리오 검색: 끝

    // 유저 검색: 시작
    @Query(value = "select u.user_seq as userSeq, u.name, u.profile_pic as profilePic, u.describe, " +
            "(select count(*) from follow where user_to = u.user_seq) as followerTotal, " +
            "(select count(*) from follow where user_from = u.user_seq) as followingTotal, " +
            "(select count(*) from follow where user_from = :myUserSeq and user_to = u.user_seq) as hasFollowed " +
            "from user u " +
            "where u.name like %:keyword% " +
            "order by followerTotal desc",
            countQuery = "select count(*) from user u where u.name like %:keyword% and :myUserSeq = :myUserSeq",
            nativeQuery = true)
    Page<SearchUserListView> searchUserOrderByFollower(@Param("myUserSeq") Long myUserSeq, @Param("keyword") String keyword, Pageable pageable);
    // 유저 검색: 끝

    // 포켓 검색 All Category 따로: 시작
    @Query(value = "select r.room_seq as roomSeq, r.thumbnail, r.name, u.user_seq as userSeq, u.name as userName, u.profile_pic as userProfilePic, " +
            "(select count(*) from room_like where room_seq = roomSeq) as `like`, " +
            "(select count(*) from room_hit where room_seq = roomSeq) as hit, " +
            "(select count(*) from room_like where room_seq = roomSeq and user_seq = :myUserSeq) as isLiked, " +
            "(select count(*) from follow where user_to = u.user_seq) as followerTotal, " +
            "group_concat(c.name) as categoryName " +
            "from room r inner join user u on r.user_seq = u.user_seq left outer join room_category rc on r.room_seq = rc.room_seq " +
            "left outer join category c on rc.category_seq = c.category_seq " +
            "where r.name like %:keyword% group by r.room_seq " +
            "order by `like` desc",
            countQuery = "select count(distinct(r.room_seq)) " +
                    "from room r inner join user u on r.user_seq = u.user_seq left outer join room_category rc on r.room_seq = rc.room_seq " +
                    "left outer join category c on rc.category_seq = c.category_seq " +
                    "where r.name like %:keyword% and :myUserSeq = :myUserSeq",
            nativeQuery = true)
    Page<SearchRoomListView> searchRoomByAllCategoryOrderByLike(@Param("myUserSeq") Long myUserSeq, @Param("keyword") String keyword, Pageable pageable);

    @Query(value = "select r.room_seq as roomSeq, r.thumbnail, r.name, u.user_seq as userSeq, u.name as userName, u.profile_pic as userProfilePic, " +
            "(select count(*) from room_like where room_seq = roomSeq) as `like`, " +
            "(select count(*) from room_hit where room_seq = roomSeq) as hit, " +
            "(select count(*) from room_like where room_seq = roomSeq and user_seq = :myUserSeq) as isLiked, " +
            "(select count(*) from follow where user_to = u.user_seq) as followerTotal, " +
            "group_concat(c.name) as categoryName " +
            "from room r inner join user u on r.user_seq = u.user_seq left outer join room_category rc on r.room_seq = rc.room_seq " +
            "left outer join category c on rc.category_seq = c.category_seq " +
            "where r.name like %:keyword% group by r.room_seq " +
            "order by hit desc",
            countQuery = "select count(distinct(r.room_seq)) " +
                    "from room r inner join user u on r.user_seq = u.user_seq left outer join room_category rc on r.room_seq = rc.room_seq " +
                    "left outer join category c on rc.category_seq = c.category_seq " +
                    "where r.name like %:keyword% and :myUserSeq = :myUserSeq",
            nativeQuery = true)
    Page<SearchRoomListView> searchRoomByAllCategoryOrderByHit(@Param("myUserSeq") Long myUserSeq, @Param("keyword") String keyword, Pageable pageable);

    @Query(value = "select r.room_seq as roomSeq, r.thumbnail, r.name, u.user_seq as userSeq, u.name as userName, u.profile_pic as userProfilePic, " +
            "(select count(*) from room_like where room_seq = roomSeq) as `like`, " +
            "(select count(*) from room_hit where room_seq = roomSeq) as hit, " +
            "(select count(*) from room_like where room_seq = roomSeq and user_seq = :myUserSeq) as isLiked, " +
            "(select count(*) from follow where user_to = u.user_seq) as followerTotal, " +
            "group_concat(c.name) as categoryName " +
            "from room r inner join user u on r.user_seq = u.user_seq left outer join room_category rc on r.room_seq = rc.room_seq " +
            "left outer join category c on rc.category_seq = c.category_seq " +
            "where r.name like %:keyword% group by r.room_seq " +
            "order by followerTotal desc",
            countQuery = "select count(distinct(r.room_seq)) " +
                    "from room r inner join user u on r.user_seq = u.user_seq left outer join room_category rc on r.room_seq = rc.room_seq " +
                    "left outer join category c on rc.category_seq = c.category_seq " +
                    "where r.name like %:keyword% and :myUserSeq = :myUserSeq",
            nativeQuery = true)
    Page<SearchRoomListView> searchRoomByAllCategoryOrderByFollowerTotal(@Param("myUserSeq") Long myUserSeq, @Param("keyword") String keyword, Pageable pageable);
    // 포켓 검색 All Category 따로: 끝
}
