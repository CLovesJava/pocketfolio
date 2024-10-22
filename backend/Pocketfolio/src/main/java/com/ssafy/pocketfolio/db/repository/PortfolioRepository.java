package com.ssafy.pocketfolio.db.repository;

import com.ssafy.pocketfolio.db.entity.Portfolio;
import com.ssafy.pocketfolio.db.repository.custom.PortfolioRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long>, PortfolioRepositoryCustom {
    // 포트폴리오 목록 조회
    List<Portfolio> findAllByUser_UserSeq(long userSeq);
}
