package com.ssafy.pocketfolio.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Log4j2
@Tag(name = "SearchPortfolioListRes", description = "포트폴리오 검색 목록 Response")
public class SearchPortfolioListRes {
    @Schema(description = "포트폴리오 번호")
    private Long portSeq;
    @Schema(description = "포트폴리오 이름")
    private String name;
    @Schema(description = "포켓 번호")
    private Long roomSeq;
    @Schema(description = "포켓 이름")
    private String roomName;
    @Schema(description = "포켓 썸네일 url")
    private String roomThumbnail;
    @Schema(description = "유저 번호")
    private Long userSeq;
    @Schema(description = "방 주인 이름")
    private String userName;
    @Schema(description = "방 주인 프로필 사진 url")
    private String userProfilePic;
    @Schema(description = "포켓 좋아요 수")
    private Long like;
    @Schema(description = "포켓 조회수")
    private Long hit;
    @Schema(description = "태그 리스트")
    private List<String> tags;

    private transient String tagsString;

    public void tagsStringToList() {
        if (StringUtils.hasText(tagsString)) {
            String[] tagArr = tagsString.split(",", 6);
            int maxLength = Math.min(tagArr.length, 5);
            tags = Arrays.asList(tagArr).subList(0, maxLength);
        }
    }

//    public SearchPortfolioListRes(SearchPortfolioListView view) {
//        portSeq = view.getPortSeq();
//        name = view.getName();
//        userSeq = view.getUserSeq();
//        userName = view.getUserName();
//        userProfilePic = view.getUserProfilePic();
//        tags = new ArrayList<>();
//        String tagStr = view.getTags();
//        if (StringUtils.hasText(tagStr)) {
//            String[] tagArr = tagStr.split(",", 6);
//            int maxLength = tagArr.length > 5 ? 5 : tagArr.length;
//            for (int i = 0; i < maxLength; i++) {
//                tags.add(tagArr[i]);
//            }
//        }
//    }
//
//    public SearchPortfolioListRes(SearchPortfolioListView view, SearchPortfolioListAddedRoomView roomView) {
//        this(view);
//        if (roomView != null) {
//            roomSeq = roomView.getRoomSeq();
//            roomName = roomView.getName();
//            roomThumbnail = roomView.getThumbnail();
//            like = roomView.getLike();
//            hit = roomView.getHit();
//        }
//    }

}
