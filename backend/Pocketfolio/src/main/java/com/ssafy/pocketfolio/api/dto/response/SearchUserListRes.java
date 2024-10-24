package com.ssafy.pocketfolio.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Tag(name = "SearchUserListRes", description = "유저 검색 목록 Response")
public class SearchUserListRes {
    @Schema(description = "유저 번호")
    private Long userSeq;
    @Schema(description = "유저 이름")
    private String name;
    @Schema(description = "유저 프로필 사진")
    private String profilePic;
    @Schema(description = "유저 자기 소개")
    private String describe;
    @Schema(description = "팔로워 수")
    private Long followerTotal;
    @Schema(description = "팔로잉 수")
    private Long followingTotal;
    @Schema(description = "팔로우 여부")
    private boolean hasFollowed;

//    public SearchUserListRes(SearchUserListView view) {
//        this.userSeq = view.getUserSeq();
//        this.name = view.getName();
//        this.profilePic = view.getProfilePic();
//        this.describe = view.getDescribe();
//        this.followerTotal = view.getFollowerTotal();
//        this.followingTotal = view.getFollowingTotal();
//        this.hasFollowed = view.getHasFollowed() > 0;
//    }
}
