package com.ssafy.pocketfolio.db.repository.custom;

import com.ssafy.pocketfolio.api.dto.response.SearchUserListRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {
    public Page<SearchUserListRes> searchUsers(Long myUserSeq, String keyword, Pageable pageable);

}
