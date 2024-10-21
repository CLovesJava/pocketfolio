package com.ssafy.pocketfolio.db.repository.custom;

import com.ssafy.pocketfolio.api.dto.response.SearchRoomListRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoomRepositoryCustom {
    public Page<SearchRoomListRes> searchRooms(Long myUserSeq, String keyword, List<Long> categories, Pageable pageable);

}
