package com.ssafy.pocketfolio.db.repository.custom;

import com.ssafy.pocketfolio.api.dto.response.SearchPortfolioListRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PortfolioRepositoryCustom {
    public Page<SearchPortfolioListRes> searchPortfolios(String keyword, Pageable pageable);

}
