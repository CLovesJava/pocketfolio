package com.ssafy.pocketfolio.api.service;

import com.ssafy.pocketfolio.api.dto.response.SearchPortfolioListRes;
import com.ssafy.pocketfolio.api.dto.response.SearchRes;
import com.ssafy.pocketfolio.api.dto.response.SearchRoomListRes;
import com.ssafy.pocketfolio.api.dto.response.SearchUserListRes;
import com.ssafy.pocketfolio.db.repository.RoomRepository;
import com.ssafy.pocketfolio.db.repository.SearchRepository;
import com.ssafy.pocketfolio.db.view.SearchPortfolioListView;
import com.ssafy.pocketfolio.db.view.SearchUserListView;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final SearchRepository searchRepository;
    private final RoomRepository roomRepository;

    private static final int SORT_ROOM_NUM_MAX = 3; // 각 sort 번호 최대값
    private static final int SORT_ROOM_BY_LIKE = 1;
    private static final int SORT_ROOM_BY_HIT = 2;
    private static final int SORT_ROOM_BY_FOLLOWER = 3;

    private static final int SORT_PORT_NUM_MAX = 2;
    private static final int SORT_PORT_BY_UPDATED = 1;
    private static final int SORT_PORT_BY_FOLLOWER = 2;

    private static final int SORT_USER_NUM_MAX = 1; // 3?
    private static final int SORT_USER_BY_FOLLOWER = 1;

    private static final int CATEGORY_TOTAL = 11; // 카테고리 총 개수
    private static final long CATEGORY_BINARY_MAX = Long.parseLong("1".repeat(CATEGORY_TOTAL), 2); // when All 카테고리 selected
    private static final int SEARCH_DEFAULT_SIZE = 20; // 한 페이지에 띄워주는 개수 (프론트에서 안 넘겼을 시)

    private static final Map<String, Map<Integer, String>> SORT_MAP = new HashMap<>();

    static {
        Map<Integer, String> roomSortMap = new HashMap<>();
        roomSortMap.put(SORT_ROOM_BY_LIKE, "like");
        roomSortMap.put(SORT_ROOM_BY_HIT, "hit");
        roomSortMap.put(SORT_ROOM_BY_FOLLOWER, "follower");
        SORT_MAP.put("room", roomSortMap);

        Map<Integer, String> portSortMap = new HashMap<>();
        portSortMap.put(SORT_PORT_BY_UPDATED, "updated");
        portSortMap.put(SORT_PORT_BY_FOLLOWER, "follower");
        SORT_MAP.put("portfolio", portSortMap);

        Map<Integer, String> userSortMap = new HashMap<>();
        userSortMap.put(SORT_USER_BY_FOLLOWER, "follower");
        SORT_MAP.put("user", userSortMap);
    }

    @Override
    public SearchRes searchRoom(Long myUserSeq, String keyword, Integer sort, Long categorySeqBinary, Integer size, Integer page) {
        SearchRes result;

        if (myUserSeq == null) {
            myUserSeq = 0L;
        }

        if (keyword == null || keyword.isEmpty()) { // QueryDSL로 바꾸면 "like %%" 자체를 없애고 검색
            keyword = ""; // like %%
        }

        ArrayList<Long> categories = new ArrayList<>();
//        boolean isAllCategory = false;
//        if (categorySeqBinary == null || categorySeqBinary < 0 || categorySeqBinary >= CATEGORY_BINARY_MAX) {
//            isAllCategory = true;
//
//            IntStream.range(1, 12).forEach(i -> categories.add(Long.valueOf(i))); // QueryDSL에서 카테고리가 비어 있으면 자동으로 전체 카테고리 선택할 예정(조건 제거)
//        }

        if (!(categorySeqBinary == null || categorySeqBinary < 0 || categorySeqBinary >= CATEGORY_BINARY_MAX)) {
            int i = 1;
            while (categorySeqBinary > 0L) {
                if (categorySeqBinary % 2 == 1) {
                    categories.add(Long.valueOf(i));
                }
                categorySeqBinary /= 2;
                i++;
            }
        }

        if (size == null || size < 1) {
            size = SEARCH_DEFAULT_SIZE;
        }

        if (page == null || page < 1) {
            page = 1;
        }
        page--;

        if (sort == null || sort < 1 || sort > SORT_ROOM_NUM_MAX) {
            sort = SORT_ROOM_BY_LIKE; // default
        }

//        Pageable pageable = PageRequest.of(page, size, JpaSort.unsafe(Sort.Direction.DESC, "like"));
        Pageable pageable = PageRequest.of(page, size, Sort.by(new Sort.Order(Sort.Direction.DESC, SORT_MAP.get("room").get(sort))));
//        Page<SearchRoomListView> viewPage;

        Page<SearchRoomListRes> rooms = roomRepository.searchRooms(myUserSeq, keyword, categories, pageable);

//        switch (sort) {
//            case SORT_ROOM_BY_HIT:
//                if (isAllCategory) {
//                    viewPage = searchRepository.searchRoomByAllCategoryOrderByHit(myUserSeq, keyword, pageable);
//                } else {
//                    viewPage = searchRepository.searchRoomOrderByHit(myUserSeq, keyword, categories, pageable);
//                }
//                break;
//            case SORT_ROOM_BY_FOLLOWER:
//                if (isAllCategory) {
//                    viewPage = searchRepository.searchRoomByAllCategoryOrderByFollowerTotal(myUserSeq, keyword, pageable);
//                } else {
//                    viewPage = searchRepository.searchRoomOrderByFollowerTotal(myUserSeq, keyword, categories, pageable);
//                }
//                break;
//            default: // SORT_ROOM_BY_LIKE
//                if (isAllCategory) {
//                    viewPage = searchRepository.searchRoomByAllCategoryOrderByLike(myUserSeq, keyword, pageable);
//                } else {
//                    viewPage = searchRepository.searchRoomOrderByLike(myUserSeq, keyword, categories, pageable);
//                }
//                break;
//        }

//        List<SearchRoomListRes> list = new ArrayList<>();
//        viewPage.getContent().forEach(e -> list.add(new SearchRoomListRes(e)));

        result = new SearchRes(rooms.getContent(), rooms.getTotalPages(), rooms.getTotalElements());

        return result;
    }

    @Override
    public SearchRes searchPortfolio(String keyword, Integer sort, Integer size, Integer page) {
        SearchRes result;

        if (keyword == null || keyword.isEmpty()) { // QueryDSL로 바꾸면 "like %%" 자체를 없애고 검색
            keyword = ""; // like %%
        }

        if (sort == null || sort < 1 || sort > SORT_PORT_NUM_MAX) {
            sort = SORT_PORT_BY_UPDATED; // default
        }

        if (size == null || size < 1) {
            size = SEARCH_DEFAULT_SIZE;
        }

        if (page == null || page < 1) {
            page = 1;
        }
        page--;

        Pageable pageable = PageRequest.of(page, size);
        Page<SearchPortfolioListView> viewPage;
        switch (sort) {
            case SORT_PORT_BY_FOLLOWER:
                viewPage = searchRepository.searchPortfolioOrderByFollowerTotal(keyword, pageable);
                break;
            default: // SORT_PORT_BY_UPDATED
                viewPage = searchRepository.searchPortfolioOrderByUpdated(keyword, pageable);
                break;
        }

        List<SearchPortfolioListRes> list = new ArrayList<>();
        viewPage.getContent().forEach(e -> {
            if (e.getArrangeSeq() != null) {
                list.add(new SearchPortfolioListRes(e, searchRepository.searchPortfolioAddedRoom(e.getArrangeSeq()).orElse(null)));
            } else {
                list.add(new SearchPortfolioListRes(e));
            }
        });

        result = new SearchRes(list, viewPage.getTotalPages(), viewPage.getTotalElements());

        return result;
    }

    @Override
    public SearchRes searchUser(Long myUserSeq, String keyword, Integer sort, Integer size, Integer page) {
        SearchRes result;

        if (myUserSeq == null) {
            myUserSeq = 0L;
        }

        if (keyword == null || keyword.isEmpty()) { // QueryDSL로 바꾸면 "like %%" 자체를 없애고 검색
            keyword = ""; // like %%
        }

        if (sort == null || sort < 1 || sort > SORT_USER_NUM_MAX) {
            sort = SORT_USER_BY_FOLLOWER; // default
        }

        if (size == null || size < 1) {
            size = SEARCH_DEFAULT_SIZE;
        }

        if (page == null || page < 1) {
            page = 1;
        }
        page--;

        Pageable pageable = PageRequest.of(page, size);
        Page<SearchUserListView> viewPage;
        switch (sort) {
            default: // SORT_USER_BY_FOLLOWER
                viewPage = searchRepository.searchUserOrderByFollower(myUserSeq, keyword, pageable);
                break;
        }

        List<SearchUserListRes> list = new ArrayList<>();
        viewPage.getContent().forEach(e -> list.add(new SearchUserListRes(e)));

        result = new SearchRes(list, viewPage.getTotalPages(), viewPage.getTotalElements());

        return result;
    }
}
